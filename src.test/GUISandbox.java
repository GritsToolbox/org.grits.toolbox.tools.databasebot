
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class GUISandbox
{

    @Inject
    public GUISandbox()
    {

    }

    @PostConstruct
    public void postConstruct(Composite parent)
    {
        parent.setLayout(new GridLayout(3, false));

        Button btnNoNewStructures = new Button(parent, SWT.RADIO);
        btnNoNewStructures.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        btnNoNewStructures.setText("No new structures");

        Button btnStructuresFromQrator = new Button(parent, SWT.RADIO);
        btnStructuresFromQrator.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        btnStructuresFromQrator.setText("Structures from Qrator");

        Button btnStructuresFromFiles = new Button(parent, SWT.RADIO);
        btnStructuresFromFiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        btnStructuresFromFiles.setText("Structures from files");
        new Label(parent, SWT.NONE);

        List list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        list.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        new Label(parent, SWT.NONE);

        Button btnAddFile = new Button(parent, SWT.NONE);
        btnAddFile.setText("Add file");

        Button btnDelete = new Button(parent, SWT.NONE);
        btnDelete.setText("Delete");

    }
}