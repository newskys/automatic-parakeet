package k.shin.domaindoma;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ex.QuickFixAction;
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
import com.intellij.psi.util.PsiUtilBase;
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
//        System.out.println("file: " + file);
//        System.out.println("fileName: " + PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> true));
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiIdentifier || element instanceof JSFile || element instanceof LeafPsiElement);
        if (position == null) {
            return false;
        }

        String key = position.getText();
//        System.out.println("key: " + key);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(project.getBasePath() + "/preset.json")), Map.class);
            Set<String> entry = object.keySet();
//            System.out.println("entry: " + entry);
            long number = entry.stream().filter(entryKey -> key.contains(entryKey)).count();
//            System.out.println("number" + number);
            return number > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private void replaceText(@NotNull Project project, Editor editor, PsiFile file) {
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiElement);
        String key = position.getText();

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

//    @Override
//    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//                // call replaceText method
//        PsiFile file = descriptor.getPsiElement().getContainingFile();
//        Editor editor = PsiUtilBase.findEditor(file);
//        replaceText(project, editor, file);
//    }

//    @Override
//    public boolean isHighPriority() {
//        return true;
//    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        replaceText(project, editor, file);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}

