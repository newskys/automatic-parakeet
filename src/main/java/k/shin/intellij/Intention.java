package k.shin.intellij;//package k.shin.renamer.renamer1;

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
    private static final String DISPLAY_NAME = "Replace with Preset Value";
    private static final String SHORT_DESCRIPTION = "Replaces the value of a function or variable name with the corresponding value from a JSON preset if the function or variable name matches the key in the preset";

//    public Intention() {
//        super(DISPLAY_NAME, SHORT_DESCRIPTION, null);
//    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "Replace with Preset Value";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "DictionaryReplacer";
    }

    @Override
    public boolean isAvailable(Project project, Editor editor, PsiFile file) {
        // Check if the cursor is on a function or variable name.
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiVariable || element instanceof PsiIdentifier);
        if (position == null) {
            return false;
        }

        String key = position.getText();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, String> object = mapper.readValue(FileUtil.loadFile(new File(project.getBasePath() + "/preset.json")), Map.class);
            Set<String> entry = object.keySet();
            long number = entry.stream().filter(entryKey -> key.contains(entryKey)).count();
            return number > 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement position = PsiTreeUtil.findFirstParent(file.findElementAt(editor.getCaretModel().getOffset()), false, element -> element instanceof PsiVariable || element instanceof PsiIdentifier);
        String key = position.getText();

//        String value = null;
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


    // Get the text to replace and the replacement text.
//    String textToReplace = "old text";
//    String replacementText = "new text";
//
//    // Get the text range of the text to replace.
//    PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
//    int startOffset = documentManager.getOffset(editor, file);
//    int endOffset = startOffset + textToReplace.length();
//
//    // Replace the text.
//    file.replace(startOffset, endOffset, replacementText);
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

//    @Override
//    public void actionPerformed(Project project, Editor editor, PsiFile file) {
//        // Get the function or variable name.
//        PsiIdentifier identifier = PsiTreeUtil.findTokenInRange(file, editor.getCaretModel().getOffset(), editor.getCaretModel().getOffset(), PsiIdentifier.class);
//        String key = identifier.getText();
//
//        // Get the value from the JSON preset.
//        JsonObject preset = JsonParser.parseString(System.getProperty("preset.json")).getAsJsonObject();
//        String value = preset.get(key).getAsString();
//
//        // Replace the function or variable name with the value from the JSON preset.
//        editor.getDocument().replaceString(identifier.getTextOffset(), identifier.getTextOffset() + identifier.getTextLength(), value);
//    }

}

