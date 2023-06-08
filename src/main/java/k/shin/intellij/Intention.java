package k.shin.intellij;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public class Intention implements IntentionAction {
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
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiVariable || element instanceof PsiIdentifier);
        if (position == null) {
            return false;
        }

        String key = position.getText();
        System.out.println("key: " + key);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(project.getBasePath() + "/preset.json")), Map.class);
            Set<String> entry = object.keySet();
            System.out.println("entry: " + entry);
            long number = entry.stream().filter(entryKey -> key.contains(entryKey)).count();
            System.out.println("number" + number);
            return number > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiVariable || element instanceof PsiIdentifier);
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

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}

