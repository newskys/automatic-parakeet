package com.sajeon.kyu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


class ReplaceIntention extends BaseIntentionAction implements HighPriorityAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Replace with preset value";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "DictionaryReplacer";
    }

    @Override
    public boolean isAvailable(Project project, Editor editor, PsiFile file) {
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiIdentifier || element instanceof JSFile || element instanceof LeafPsiElement);
        if (position == null) {
            return false;
        }


        String key = position.getText();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(project.getBasePath() + "/preset.json")), Map.class);
            Set<String> entry = object.keySet();
            Optional<String> targetKey = entry.stream().filter(entryKey -> key.contains(entryKey)).findFirst();
            if (targetKey.isEmpty()) return false;
            String value = object.get(targetKey.get());
            SharedState.getInstance().setFrom(key);
            SharedState.getInstance().setTo(value);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void replaceText(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiElement);
        String key = position.getText();

        if (key.equals(SharedState.getInstance().getFrom())) {
            editor.getDocument().replaceString(position.getTextOffset(), position.getTextOffset() + position.getTextLength(), SharedState.getInstance().getTo());
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(project.getBasePath() + "/preset.json")), Map.class);
            Set<String> entry = object.keySet();
            Optional<String> targetKey = entry.stream().filter(entryKey -> key.contains(entryKey)).findFirst();
            if (targetKey == null || targetKey.isEmpty()) return;
            String value = object.get(targetKey.get());
            editor.getDocument().replaceString(position.getTextOffset(), position.getTextOffset() + position.getTextLength(), key.replace(targetKey.get(), value));
        } catch (IOException e) {
            return;
        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        replaceText(project, editor, file);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}

