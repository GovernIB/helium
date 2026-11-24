package es.caib.helium.intellij;

import java.util.Map;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

/**
 * New → Diagrama BPMN de Helium: crea un fitxer {@code .bpmn} amb un procés executable i l'esdeveniment d'inici, com
 * el diagrama nou de l'editor de Helium. El nom del procés és el del fitxer i l'identificador se'n deriva
 * ({@link FitxersBpmn#idProces}).
 *
 * <p>El contingut surt de la plantilla interna {@value #PLANTILLA} ({@code fileTemplates/internal}), que es pot
 * modificar a Settings → Editor → File and Code Templates.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class NouFitxerBpmnAction extends CreateFileFromTemplateAction implements DumbAware {

	static final String PLANTILLA = "Helium BPMN";
	static final Icon ICONA = IconLoader.getIcon("/icons/bpmn.svg", NouFitxerBpmnAction.class);

	public NouFitxerBpmnAction() {
		super("Diagrama BPMN de Helium", "Crea un diagrama BPMN de Helium amb l'esdeveniment d'inici", ICONA);
	}

	@Override
	protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory,
			CreateFileFromTemplateDialog.@NotNull Builder builder) {
		builder.setTitle("Nou diagrama BPMN de Helium")
				.addKind("Diagrama BPMN", ICONA, PLANTILLA);
	}

	@Override
	protected @NotNull String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
		return "Crear el diagrama BPMN " + newName;
	}

	@Override
	protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
		return crear(name, template, dir, true);
	}

	/** Crea el fitxer amb la plantilla del plugin (s'ha de cridar dins una comanda d'escriptura). */
	static PsiFile crear(String nom, PsiDirectory directori, boolean obrir) {
		FileTemplate plantilla = FileTemplateManager.getInstance(directori.getProject()).getInternalTemplate(PLANTILLA);
		return crear(nom, plantilla, directori, obrir);
	}

	private static PsiFile crear(String nom, FileTemplate plantilla, PsiDirectory directori, boolean obrir) {
		String nomProces = FitxersBpmn.nomProces(nom);
		// Els valors van dins atributs XML de la plantilla
		Map<String, String> propietats = Map.of(
				"PROCESS_ID", FitxersBpmn.idProces(nomProces),
				"PROCESS_NAME", StringUtil.escapeXmlEntities(nomProces));
		return createFileFromTemplate(nom, plantilla, directori, null, obrir, Map.of(), propietats);
	}

}
