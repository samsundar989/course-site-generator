package cg;

import djf.AppTemplate;
import djf.components.AppClipboardComponent;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import djf.components.AppWorkspaceComponent;
import java.util.Locale;
import cg.data.CGData;
import cg.files.CGFiles;
import cg.clipboard.CGClipboard;
import cg.workspace.CGWorkspace;
import static javafx.application.Application.launch;

public class CGApp extends AppTemplate {   
    /**
     * This is where program execution begins. Since this is a JavaFX app it
     * will simply call launch, which gets JavaFX rolling, resulting in sending
     * the properly initialized Stage (i.e. window) to the start method inherited
     * from AppTemplate, defined in the Desktop Java Framework.
     * 
     * @param args Command-line arguments, there are no such settings used
     * by this application.
     */
    public static void main(String[] args) {
	Locale.setDefault(Locale.US);
	launch(args);
    }

    @Override
    public AppClipboardComponent buildClipboardComponent(AppTemplate app) {
        return new CGClipboard(this);
    }

    @Override
    public AppDataComponent buildDataComponent(AppTemplate app) {
        return new CGData(this);
    }

    @Override
    public AppFileComponent buildFileComponent() {
        return new CGFiles(this);
    }

    @Override
    public AppWorkspaceComponent buildWorkspaceComponent(AppTemplate app) {
        return new CGWorkspace(this);        
    }
}