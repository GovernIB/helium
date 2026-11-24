package es.caib.helium.intellij;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Afegeix la pestanya "Disseny" (editor visual de Helium) als fitxers BPMN, davant de la pestanya de text.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class HeliumBpmnEditorProvider implements FileEditorProvider, DumbAware {

	@Override
	public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
		return FitxersBpmn.esBpmn(file);
	}

	@Override
	public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
		return new HeliumBpmnEditor(project, file);
	}

	@Override
	public @NotNull String getEditorTypeId() {
		return "helium-bpmn-disseny";
	}

	@Override
	public @NotNull FileEditorPolicy getPolicy() {
		return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
	}

}
