package cg.workspace;

import djf.components.AppWorkspaceComponent;
import djf.modules.AppFoolproofModule;
import djf.modules.AppGUIModule;
import static djf.modules.AppGUIModule.ENABLED;
import djf.ui.AppNodesBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import properties_manager.PropertiesManager;
import cg.CGApp;
import cg.CGPropertyType;
import static cg.CGPropertyType.*;
import cg.data.CGData;
import cg.data.Meeting;
import cg.data.TeachingAssistantPrototype;
import cg.data.TimeSlot;
import cg.workspace.controllers.CGController;
import cg.workspace.dialogs.TADialog;
import cg.workspace.foolproof.CGFoolproofDesign;
import static cg.workspace.style.CGStyle.*;
import djf.modules.AppFileModule;
import djf.ui.controllers.AppFileController;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author McKillaGorilla
 */
public class CGWorkspace extends AppWorkspaceComponent {

    public CGWorkspace(CGApp app) {
        super(app);

        // LAYOUT THE APP
        initLayout();

        // INIT THE EVENT HANDLERS
        initControllers();

        // 
        initFoolproofDesign();

        // INIT DIALOGS
        initDialogs();
    }

    private void initDialogs() {
        TADialog taDialog = new TADialog((CGApp) app);
        app.getGUIModule().addDialog(CG_TA_EDIT_DIALOG, taDialog);
    }

    // THIS HELPER METHOD INITIALIZES ALL THE CONTROLS IN THE WORKSPACE
    private void initLayout() {
        // FIRST LOAD THE FONT FAMILIES FOR THE COMBO BOX
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // THIS WILL BUILD ALL OF OUR JavaFX COMPONENTS FOR US
        AppNodesBuilder cgBuilder = app.getGUIModule().getNodesBuilder();

        // INIT MAIN PANE
        VBox mainPane = cgBuilder.buildVBox(CG_MAIN_PANE, null, CLASS_CG_PANE, ENABLED);

        TabPane tabPane = cgBuilder.buildTabPane(CG_TAB_PANE, mainPane, CLASS_CG_TAB_PANE, ENABLED);
        Tab siteTab = cgBuilder.buildTab(CG_SITE_TAB, tabPane, CLASS_CG_TAB, ENABLED, "Site");
        Tab syllabusTab = cgBuilder.buildTab(CG_SYLLABUS_TAB, tabPane, CLASS_CG_TAB, ENABLED, "Syllabus");
        Tab meetingsTab = cgBuilder.buildTab(CG_MEETINGS_TAB, tabPane, CLASS_CG_TAB, ENABLED, "Meeting Times");
        Tab ohTab = cgBuilder.buildTab(CG_OH_TAB, tabPane, CLASS_CG_TAB, ENABLED, "Office Hours");
        Tab scheduleTab = cgBuilder.buildTab(CG_SCHEDULE_TAB, tabPane, CLASS_CG_TAB, ENABLED, "Schedule");
        tabPane.tabMinWidthProperty().bind(tabPane.widthProperty().multiply(.945 / 5.0));
        ScrollPane siteScroll = cgBuilder.buildTabScrollPane(CG_SITE_SCROLL, siteTab, CLASS_CG_SCROLL_PANE, ENABLED);
        siteScroll.setFitToWidth(ENABLED);
        siteScroll.setFitToHeight(ENABLED);
        ScrollPane syllabusScroll = cgBuilder.buildTabScrollPane(CG_SYLLABUS_SCROLL, syllabusTab, CLASS_CG_SCROLL_PANE, ENABLED);
        syllabusScroll.setFitToWidth(ENABLED);
        syllabusScroll.setFitToHeight(ENABLED);
        ScrollPane meetingsScroll = cgBuilder.buildTabScrollPane(CG_MEETINGS_SCROLL, meetingsTab, CLASS_CG_SCROLL_PANE, ENABLED);
        meetingsScroll.setFitToWidth(ENABLED);
        meetingsScroll.setFitToHeight(ENABLED);
        ScrollPane ohScroll = cgBuilder.buildTabScrollPane(CG_OH_SCROLL, ohTab, CLASS_CG_SCROLL_PANE, ENABLED);
        ohScroll.setFitToWidth(ENABLED);
        ohScroll.setFitToHeight(ENABLED);
        ScrollPane scheduleScroll = cgBuilder.buildTabScrollPane(CG_SCHEDULE_SCROLL, scheduleTab, CLASS_CG_SCROLL_PANE, ENABLED);
        scheduleScroll.setFitToWidth(ENABLED);
        scheduleScroll.setFitToHeight(ENABLED);
        VBox siteBox = cgBuilder.buildScrollVBox(CG_SITE_BOX, siteScroll, CLASS_CG_TAB_BOX, ENABLED);
        VBox syllabusBox = cgBuilder.buildScrollVBox(CG_SYLLABUS_BOX, syllabusScroll, CLASS_CG_TAB_BOX, ENABLED);
        VBox meetingsBox = cgBuilder.buildScrollVBox(CG_MEETINGS_BOX, meetingsScroll, CLASS_CG_TAB_BOX, ENABLED);
        VBox ohBox = cgBuilder.buildScrollVBox(CG_OH_BOX, ohScroll, CLASS_CG_TAB_BOX, ENABLED);
        VBox scheduleBox = cgBuilder.buildScrollVBox(CG_SCHEDULE_BOX, scheduleScroll, CLASS_CG_TAB_BOX, ENABLED);

        populateSiteTab(siteTab, siteBox, siteScroll);
        populateSyllabusTab(syllabusTab, syllabusBox, syllabusScroll);
        populateMeetingsTab(meetingsTab, meetingsBox, meetingsScroll);
        populateOHTab(ohTab, ohBox, ohScroll);
        populateScheduleTab(scheduleTab, scheduleBox, scheduleScroll);

//        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
//        VBox.setVgrow(taTable, Priority.ALWAYS);
        // MAKE SURE IT'S THE TABLE THAT ALWAYS GROWS IN THE LEFT PANE
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // BOTH PANES WILL NOW GO IN A SPLIT PANE
        workspace = new BorderPane();

        // AND PUT EVERYTHING IN THE WORKSPACE
        ((BorderPane) workspace).setCenter(mainPane);
    }

    private void populateSiteTab(Tab siteTab, VBox siteBox, ScrollPane scrollPane) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        siteBox.setId(CG_SITE_BOX.toString());
        siteBox.getStyleClass().add(CLASS_CG_TAB_BOX);
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("CG_BANNER_LABEL", "CG_PAGES_LABEL", "CG_STYLE_LABEL", "CG_INSTRUCTOR_LABEL"));
        siteBox.setPadding(new Insets(10, 5, 10, 5));

        VBox banner = builder.buildVBox(CG_CARD, siteBox, CLASS_CG_CARD, ENABLED);
        banner.setSpacing(20);
        builder.buildLabel(CG_BANNER_LABEL, banner, CLASS_CG_HEADER_LABEL, ENABLED);
        HBox pages = builder.buildHBox(CG_CARD, siteBox, CLASS_CG_CARD, ENABLED);
        pages.setSpacing(30);
        VBox style = builder.buildVBox(CG_CARD, siteBox, CLASS_CG_CARD, ENABLED);
        style.setSpacing(20);
        VBox instructor = builder.buildVBox(CG_CARD, siteBox, CLASS_CG_CARD, ENABLED);
        instructor.setSpacing(20);
        builder.buildLabel(CG_PAGES_LABEL, pages, CLASS_CG_HEADER_LABEL, ENABLED);
        builder.buildLabel(CG_STYLE_LABEL, style, CLASS_CG_HEADER_LABEL, ENABLED);
        builder.buildLabel(CG_INSTRUCTOR_LABEL, instructor, CLASS_CG_HEADER_LABEL, ENABLED);

        //Banner UI
        HBox firstRow = builder.buildHBox(this, banner, EMPTY_TEXT, ENABLED);
        firstRow.setSpacing(30);
        HBox secondRow = builder.buildHBox(this, banner, EMPTY_TEXT, ENABLED);
        secondRow.setSpacing(20);
        HBox thirdRow = builder.buildHBox(this, banner, EMPTY_TEXT, ENABLED);
        thirdRow.setSpacing(20);
        HBox fourthRow = builder.buildHBox(this, banner, EMPTY_TEXT, ENABLED);
        fourthRow.setSpacing(20);

        Label subLabel = builder.buildLabel(CG_SUBJECT_LABEL, firstRow, CLASS_CG_BOLD, ENABLED);
        ComboBox subject = builder.buildComboBox(this, BUTTON_TAG_WIDTH, CLASS_CG_PROMPT, firstRow, EMPTY_TEXT, ENABLED);
        subject.setEditable(ENABLED);
        Label numLabel = builder.buildLabel(CG_NUMBER_LABEL, firstRow, CLASS_CG_BOLD, ENABLED);
        ComboBox number = builder.buildComboBox(this, BUTTON_TAG_WIDTH, CLASS_CG_PROMPT, firstRow, EMPTY_TEXT, ENABLED);
        number.setEditable(ENABLED);
        Label semesterLabel = builder.buildLabel(CG_SEMESTER_LABEL, secondRow, CLASS_CG_BOLD, ENABLED);
        ComboBox semester = builder.buildComboBox(this, BUTTON_TAG_WIDTH, CLASS_CG_PROMPT, secondRow, EMPTY_TEXT, ENABLED);
        ArrayList<String> semesters = new ArrayList<>(Arrays.asList("Fall", "Winter", "Spring", "Summer I", "Summer II"));
        semester.getItems().addAll(semesters);
        Label yearLabel = builder.buildLabel(CG_YEAR_LABEL, secondRow, CLASS_CG_BOLD, ENABLED);
        ComboBox yearBox = builder.buildComboBox(this, BUTTON_TAG_WIDTH, CLASS_CG_PROMPT, secondRow, EMPTY_TEXT, ENABLED);
        yearBox.setEditable(ENABLED);
        Label title = builder.buildLabel(CG_BANNER_TITLE_LABEL, thirdRow, CLASS_CG_BOLD, ENABLED);
        TextField titleField = builder.buildTextField(CG_TITLE_TEXT_FIELD, thirdRow, EMPTY_TEXT, ENABLED);
        Label export = builder.buildLabel(CG_EXPORT_LABEL, fourthRow, EMPTY_TEXT, ENABLED);
        String exportText = ".";
        //StringBuilder exportBuilder = new StringBuilder().append(exportText).append("\\").append("\\");
        //Label dir = builder.buildLabel(exportBuilder.toString(), fourthRow, CLASS_CG_TEXT, ENABLED);

        //Pages UI
        CheckBox boxHome = new CheckBox();
        boxHome.setText("Home");
        boxHome.getStyleClass().add(CLASS_CG_COMBO_BOX);
        CheckBox boxSyllabus = new CheckBox();
        CheckBox boxSchedule = new CheckBox();
        CheckBox boxHW = new CheckBox();
        boxSyllabus.setText("Syllabus");
        boxSchedule.setText("Schedule");
        boxHW.setText("HW");
        boxSyllabus.getStyleClass().add(CLASS_CG_COMBO_BOX);
        boxSchedule.getStyleClass().add(CLASS_CG_COMBO_BOX);
        boxHW.getStyleClass().add(CLASS_CG_COMBO_BOX);
        pages.setAlignment(Pos.CENTER_LEFT);
        pages.getChildren().addAll(boxHome, boxSyllabus, boxSchedule, boxHW);

        //Style UI
        Button favButton = builder.buildTextButton(CG_FAV_BUTTON, style, CLASS_CG_BUTTON, ENABLED);
        Button navButton = builder.buildTextButton(CG_NAV_BUTTON, style, CLASS_CG_BUTTON, ENABLED);
        Button leftButton = builder.buildTextButton(CG_LEFT_BUTTON, style, CLASS_CG_BUTTON, ENABLED);
        Button rightButton = builder.buildTextButton(CG_RIGHT_BUTTON, style, CLASS_CG_BUTTON, ENABLED);
        HBox fontsBox = builder.buildHBox(this, style, EMPTY_TEXT, ENABLED);
        fontsBox.setSpacing(20);
        Label cssLabel = builder.buildLabel(CG_CSS_LABEL, fontsBox, CLASS_CG_BOLD, ENABLED);
        ComboBox cssBox = builder.buildComboBox(CG_CSS_BOX, BUTTON_TAG_WIDTH, leftButton, fontsBox, EMPTY_TEXT, ENABLED);
        
//        File f = new File("../work/css");
//        File[] matchingFiles = f.listFiles();
//        for(File file:matchingFiles){
//            cssBox.getItems().add(file.getName());
//        }
        
        
        Label noteLabel = builder.buildLabel(CG_NOTE_LABEL, style, CLASS_CG_BOLD, ENABLED);

        //Instructor UI
        HBox instructorRowOne = builder.buildHBox(this, instructor, EMPTY_TEXT, ENABLED);
        instructorRowOne.setSpacing(20);
        HBox instructorRowTwo = builder.buildHBox(this, instructor, EMPTY_TEXT, ENABLED);
        instructorRowTwo.setSpacing(20);
        HBox instructorRowThree = builder.buildHBox(this, instructor, EMPTY_TEXT, ENABLED);
        instructorRowThree.setSpacing(20);
        HBox instructorRowFour = builder.buildHBox(this, instructor, EMPTY_TEXT, ENABLED);
        Label nameLabel = builder.buildLabel(CG_NAME_LABEL, instructorRowOne, CLASS_CG_BOLD, ENABLED);
        TextField nameField = builder.buildTextField(CG_INSTRUCTOR_NAME_TEXT_FIELD, instructorRowOne, EMPTY_TEXT, ENABLED);
        Label roomLabel = builder.buildLabel(CG_ROOM_LABEL, instructorRowOne, CLASS_CG_BOLD, ENABLED);
        TextField roomField = builder.buildTextField(CG_ROOM_TEXT_FIELD, instructorRowOne, EMPTY_TEXT, ENABLED);
        Label emailLabel = builder.buildLabel(CG_EMAIL_LABEL, instructorRowTwo, CLASS_CG_BOLD, ENABLED);
        TextField emailfield = builder.buildTextField(CG_INSTRUCTOR_EMAIL_TEXT_FIELD, instructorRowTwo, EMPTY_TEXT, ENABLED);
        Label homeLabel = builder.buildLabel(CG_HOME_LABEL, instructorRowTwo, CLASS_CG_BOLD, ENABLED);
        TextField homeField = builder.buildTextField(CG_HOME_TEXT_FIELD, instructorRowTwo, EMPTY_TEXT, ENABLED);
        Button ohButton = builder.buildIconButton(CG_OH_BUTTON, instructorRowThree, CLASS_CG_MORE_BUTTON, ENABLED);
        Label ohLabel = builder.buildLabel(CG_OH_LABEL, instructorRowThree, CLASS_CG_BOLD, ENABLED);
        TextField ohField = builder.buildTextField(CG_OH_TEXT_FIELD, instructorRowFour, EMPTY_TEXT, ENABLED);
        ohField.visibleProperty().set(false);

        siteTab.setContent(scrollPane);

    }

    private void populateSyllabusTab(Tab syllabusTab, VBox syllabusBox, ScrollPane scrollPane) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        syllabusBox.setId(CG_SYLLABUS_BOX.toString());
        syllabusBox.getStyleClass().add(CLASS_CG_TAB_BOX);
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("CG_DESCRIPTION_LABEL", "CG_TOPICS_LABEL", "CG_PREREQ_LABEL", "CG_OUTCOMES_LABEL",
                "CG_TEXTBOOKS_LABEL", "CG_COMPONENTS_LABEL", "CG_GRADING_LABEL", "CG_DISHONESTY_LABEL", "CG_ASSISTANCE_LABEL"));
        ArrayList<String> buttonTitles = new ArrayList<>(Arrays.asList("CG_DESCRIPTION_BUTTON", "CG_TOPICS_BUTTON", "CG_PREREQ_BUTTON", "CG_OUTCOMES_BUTTON",
                "CG_TEXTBOOKS_BUTTON", "CG_COMPONENTS_BUTTON", "CG_GRADING_BUTTON", "CG_DISHONESTY_BUTTON", "CG_ASSISTANCE_BUTTON"));
        syllabusBox.setPadding(new Insets(10, 5, 10, 5));
        for (int i = 0; i < 9; i++) {
            builder.buildHBox(CG_CARD, syllabusBox, CLASS_CG_CARD, ENABLED);
            Button button = builder.buildIconButton(CG_MORE_BUTTON, (HBox) syllabusBox.getChildren().get(i), CLASS_CG_MORE_BUTTON, ENABLED);
            builder.buildLabel(titles.get(i), (HBox) syllabusBox.getChildren().get(i), CLASS_CG_HEADER_LABEL, ENABLED);
        }

        syllabusTab.setContent(scrollPane);
    }

    private void populateMeetingsTab(Tab meetingsTab, VBox meetingsBox, ScrollPane scrollPane) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        meetingsBox.setId(CG_MEETINGS_BOX.toString());
        meetingsBox.getStyleClass().add(CLASS_CG_TAB_BOX);
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("CG_LECTURE_LABEL", "CG_RECITATION_LABEL", "CG_LAB_LABEL"));
        meetingsBox.setPadding(new Insets(10, 5, 10, 5));

        VBox lectures = builder.buildVBox(CG_CARD, meetingsBox, CLASS_CG_CARD, ENABLED);

        HBox lectureHeader = builder.buildHBox(this, lectures, EMPTY_TEXT, ENABLED);
        builder.buildIconButton(CG_MORE_BUTTON, lectureHeader, CLASS_CG_MORE_BUTTON, ENABLED);
        builder.buildIconButton(CG_LESS_BUTTON, lectureHeader, CLASS_CG_LESS_BUTTON, ENABLED);
        builder.buildLabel(CG_LECTURE_LABEL, lectureHeader, CLASS_CG_HEADER_LABEL, ENABLED);

        VBox recitations = builder.buildVBox(CG_CARD, meetingsBox, CLASS_CG_CARD, ENABLED);
        HBox recitationHeader = builder.buildHBox(this, recitations, EMPTY_TEXT, ENABLED);
        builder.buildIconButton(CG_MORE_BUTTON, recitationHeader, CLASS_CG_MORE_BUTTON, ENABLED);
        builder.buildIconButton(CG_LESS_BUTTON, recitationHeader, CLASS_CG_LESS_BUTTON, ENABLED);
        builder.buildLabel(CG_RECITATION_LABEL, recitationHeader, CLASS_CG_HEADER_LABEL, ENABLED);

        VBox labs = builder.buildVBox(CG_CARD, meetingsBox, CLASS_CG_CARD, ENABLED);
        HBox labHeader = builder.buildHBox(this, labs, EMPTY_TEXT, ENABLED);
        builder.buildIconButton(CG_MORE_BUTTON, labHeader, CLASS_CG_MORE_BUTTON, ENABLED);
        builder.buildIconButton(CG_LESS_BUTTON, labHeader, CLASS_CG_LESS_BUTTON, ENABLED);
        builder.buildLabel(CG_LAB_LABEL, labHeader, CLASS_CG_HEADER_LABEL, ENABLED);

        //Lectures Table
        TableView<Meeting> lecturesTable = builder.buildTableView(CG_LECTURES_TABLE_VIEW, lectures, CLASS_CG_TABLE_VIEW, ENABLED);
        lecturesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn sectionLecturesColumn = builder.buildTableColumn(CG_SECTION_LECTURES_TABLE_COLUMN, lecturesTable, CLASS_CG_COLUMN);
        TableColumn daysLecturesColumn = builder.buildTableColumn(CG_DAYS_LECTURES_TABLE_COLUMN, lecturesTable, CLASS_CG_COLUMN);
        TableColumn timeLecturesColumn = builder.buildTableColumn(CG_TIME_LECTURES_TABLE_COLUMN, lecturesTable, CLASS_CG_CENTERED_COLUMN);
        TableColumn roomLecturesColumn = builder.buildTableColumn(CG_ROOM_LECTURES_TABLE_COLUMN, lecturesTable, CLASS_CG_COLUMN);
        sectionLecturesColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        daysLecturesColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days"));
        timeLecturesColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Time"));
        roomLecturesColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        sectionLecturesColumn.prefWidthProperty().bind(lecturesTable.widthProperty().multiply(1.0 / 5.0));
        daysLecturesColumn.prefWidthProperty().bind(lecturesTable.widthProperty().multiply(2.0 / 5.0));
        timeLecturesColumn.prefWidthProperty().bind(lecturesTable.widthProperty().multiply(1.0 / 5.0));
        roomLecturesColumn.prefWidthProperty().bind(lecturesTable.widthProperty().multiply(1.0 / 5.0));

        //Recitations Table
        TableView<Meeting> recitationsTable = builder.buildTableView(CG_RECITATIONS_TABLE_VIEW, recitations, CLASS_CG_TABLE_VIEW, ENABLED);
        recitationsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn sectionRecitationColumn = builder.buildTableColumn(CG_SECTION_RECITATIONS_TABLE_COLUMN, recitationsTable, CLASS_CG_COLUMN);
        TableColumn daysRecitationColumn = builder.buildTableColumn(CG_DAYS_RECITATIONS_TABLE_COLUMN, recitationsTable, CLASS_CG_COLUMN);
        TableColumn roomRecitationColumn = builder.buildTableColumn(CG_ROOM_RECITATIONS_TABLE_COLUMN, recitationsTable, CLASS_CG_CENTERED_COLUMN);
        TableColumn taOneRecitationColumn = builder.buildTableColumn(CG_TA1_RECITATIONS_TABLE_COLUMN, recitationsTable, CLASS_CG_COLUMN);
        TableColumn taTwoRecitationColumn = builder.buildTableColumn(CG_TA2_RECITATIONS_TABLE_COLUMN, recitationsTable, CLASS_CG_COLUMN);
        sectionRecitationColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        daysRecitationColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days & Time"));
        roomRecitationColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        taOneRecitationColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA1"));
        taTwoRecitationColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA2"));
        sectionRecitationColumn.prefWidthProperty().bind(recitationsTable.widthProperty().multiply(1.0 / 5.0));
        daysRecitationColumn.prefWidthProperty().bind(recitationsTable.widthProperty().multiply(2.0 / 5.0));
        roomRecitationColumn.prefWidthProperty().bind(recitationsTable.widthProperty().multiply(1.0 / 5.0));
        taOneRecitationColumn.prefWidthProperty().bind(recitationsTable.widthProperty().multiply(1.0 / 5.0));
        taTwoRecitationColumn.prefWidthProperty().bind(recitationsTable.widthProperty().multiply(1.0 / 5.0));

        //Labs Table
        TableView<Meeting> labsTable = builder.buildTableView(CG_LABS_TABLE_VIEW, labs, CLASS_CG_TABLE_VIEW, ENABLED);
        labsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn sectionLabsColumn = builder.buildTableColumn(CG_SECTION_LAB_TABLE_COLUMN, labsTable, CLASS_CG_COLUMN);
        TableColumn daysLabsColumn = builder.buildTableColumn(CG_DAY_LAB_TABLE_COLUMN, labsTable, CLASS_CG_COLUMN);
        TableColumn roomLabsColumn = builder.buildTableColumn(CG_ROOM_LAB_TABLE_COLUMN, labsTable, CLASS_CG_CENTERED_COLUMN);
        TableColumn taOneLabsColumn = builder.buildTableColumn(CG_TA1_LAB_TABLE_COLUMN, labsTable, CLASS_CG_COLUMN);
        TableColumn taTwoLabsColumn = builder.buildTableColumn(CG_TA2_LAB_TABLE_COLUMN, labsTable, CLASS_CG_COLUMN);
        sectionLabsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Section"));
        daysLabsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Days & Time"));
        roomLabsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Room"));
        taOneLabsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA1"));
        taTwoLabsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("TA2"));
        sectionLabsColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1.0 / 5.0));
        daysLabsColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(2.0 / 5.0));
        roomLabsColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1.0 / 5.0));
        taOneLabsColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1.0 / 5.0));
        taTwoLabsColumn.prefWidthProperty().bind(labsTable.widthProperty().multiply(1.0 / 5.0));

        meetingsTab.setContent(scrollPane);
    }

    private void populateOHTab(Tab ohTab, VBox ohBox, ScrollPane scrollPane) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        ohBox.setId(CG_OH_BOX.toString());
        ohBox.getStyleClass().add(CLASS_CG_TAB_BOX);
        VBox ohCard = builder.buildVBox(CG_CARD, ohBox, CLASS_CG_CARD, ENABLED);
        ohBox.setPadding(new Insets(10, 5, 10, 5));

        // INIT TAS HEADER
        HBox tasHeaderBox = builder.buildHBox(CG_TAS_HEADER_PANE, ohCard, CLASS_CG_BOX, ENABLED);
        tasHeaderBox.setSpacing(20);
        builder.buildIconButton(CG_TAS_LESS_BUTTON, tasHeaderBox, CLASS_CG_LESS_BUTTON, ENABLED);
        builder.buildLabel(CGPropertyType.CG_TAS_HEADER_LABEL, tasHeaderBox, CLASS_CG_HEADER_LABEL, ENABLED);
        HBox typeHeaderBox = builder.buildHBox(CG_GRAD_UNDERGRAD_TAS_PANE, tasHeaderBox, CLASS_CG_RADIO_BOX, ENABLED);
        ToggleGroup tg = new ToggleGroup();
        builder.buildRadioButton(CG_ALL_RADIO_BUTTON, typeHeaderBox, CLASS_CG_RADIO_BUTTON, ENABLED, tg, true);
        builder.buildRadioButton(CG_GRAD_RADIO_BUTTON, typeHeaderBox, CLASS_CG_RADIO_BUTTON, ENABLED, tg, false);
        builder.buildRadioButton(CG_UNDERGRAD_RADIO_BUTTON, typeHeaderBox, CLASS_CG_RADIO_BUTTON, ENABLED, tg, false);

        //TAs Table
        TableView<TeachingAssistantPrototype> taTable = builder.buildTableView(CG_TAS_TABLE_VIEW, ohCard, CLASS_CG_TABLE_VIEW, ENABLED);
        taTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn nameColumn = builder.buildTableColumn(CG_NAME_TABLE_COLUMN, taTable, CLASS_CG_COLUMN);
        TableColumn emailColumn = builder.buildTableColumn(CG_EMAIL_TABLE_COLUMN, taTable, CLASS_CG_COLUMN);
        TableColumn slotsColumn = builder.buildTableColumn(CG_SLOTS_TABLE_COLUMN, taTable, CLASS_CG_CENTERED_COLUMN);
        TableColumn typeColumn = builder.buildTableColumn(CG_TYPE_TABLE_COLUMN, taTable, CLASS_CG_COLUMN);
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Email"));
        slotsColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Slots"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("Type"));
        nameColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
        emailColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(2.0 / 5.0));
        slotsColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));
        typeColumn.prefWidthProperty().bind(taTable.widthProperty().multiply(1.0 / 5.0));

        // ADD BOX FOR ADDING A TA
        HBox taBox = builder.buildHBox(CG_ADD_TA_PANE, ohCard, CLASS_CG_PANE, ENABLED);
        taBox.setSpacing(80);
        builder.buildTextField(CG_NAME_TEXT_FIELD, taBox, CLASS_CG_TEXT_FIELD, ENABLED);
        builder.buildTextField(CG_EMAIL_TEXT_FIELD, taBox, CLASS_CG_TEXT_FIELD, ENABLED);
        builder.buildTextButton(CG_ADD_TA_BUTTON, taBox, CLASS_CG_BUTTON, ENABLED);

        // INIT OFFICE HOURS HEADER
        HBox officeHoursHeaderBox = builder.buildHBox(CG_OFFICE_HOURS_HEADER_PANE, ohCard, CLASS_CG_PANE, ENABLED);
        officeHoursHeaderBox.setSpacing(50);
        builder.buildLabel(CG_OFFICE_HOURS_HEADER_LABEL, officeHoursHeaderBox, CLASS_CG_HEADER_LABEL, ENABLED);
        builder.buildLabel(CG_START_TIME_LABEL, officeHoursHeaderBox, CLASS_CG_HEADER_LABEL, ENABLED);
        ComboBox startTime = builder.buildComboBox(CG_START_TIME_COMBOBOX, BUTTON_TAG_WIDTH, emailColumn, officeHoursHeaderBox, EMPTY_TEXT, ENABLED);
        builder.buildLabel(CG_END_TIME_LABEL, officeHoursHeaderBox, CLASS_CG_HEADER_LABEL, ENABLED);
        ComboBox endTime = builder.buildComboBox(CG_END_TIME_COMBOBOX, BUTTON_TAG_WIDTH, emailColumn, officeHoursHeaderBox, EMPTY_TEXT, ENABLED);
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int i = 7; i < 24; i++) {
            String time = "";
            if (i > 12) {
                int x = i - 12;
                time = Integer.toString(x).concat(":00pm");
            } else {
                time = Integer.toString(i).concat(":00am");
            }
            times.add(time);
        }
        startTime.getItems().addAll(times);
        endTime.getItems().addAll(times);

        //Office Hours Table
        TableView<TimeSlot> officeHoursTable = builder.buildTableView(CG_OFFICE_HOURS_TABLE_VIEW, ohCard, CLASS_CG_OFFICE_HOURS_TABLE_VIEW, ENABLED);
        setupOfficeHoursColumn(CG_START_TIME_TABLE_COLUMN, officeHoursTable, CLASS_CG_TIME_COLUMN, "startTime");
        setupOfficeHoursColumn(CG_END_TIME_TABLE_COLUMN, officeHoursTable, CLASS_CG_TIME_COLUMN, "endTime");
        setupOfficeHoursColumn(CG_MONDAY_TABLE_COLUMN, officeHoursTable, CLASS_CG_DAY_OF_WEEK_COLUMN, "monday");
        setupOfficeHoursColumn(CG_TUESDAY_TABLE_COLUMN, officeHoursTable, CLASS_CG_DAY_OF_WEEK_COLUMN, "tuesday");
        setupOfficeHoursColumn(CG_WEDNESDAY_TABLE_COLUMN, officeHoursTable, CLASS_CG_DAY_OF_WEEK_COLUMN, "wednesday");
        setupOfficeHoursColumn(CG_THURSDAY_TABLE_COLUMN, officeHoursTable, CLASS_CG_DAY_OF_WEEK_COLUMN, "thursday");
        setupOfficeHoursColumn(CG_FRIDAY_TABLE_COLUMN, officeHoursTable, CLASS_CG_DAY_OF_WEEK_COLUMN, "friday");

        ohTab.setContent(scrollPane);

    }

    private void populateScheduleTab(Tab scheduleTab, VBox scheduleBox, ScrollPane scrollPane) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        scheduleBox.setId(CG_SCHEDULE_BOX.toString());
        scheduleBox.getStyleClass().add(CLASS_CG_TAB_BOX);
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("CG_CALENDAR_LABEL", "CG_SCHEDULE_LABEL", "CG_ADD_LABEL"));
        scheduleBox.setPadding(new Insets(10, 5, 10, 5));

        VBox calendar = builder.buildVBox(CG_CARD, scheduleBox, CLASS_CG_CARD, ENABLED);
        builder.buildLabel(CG_CALENDAR_LABEL, calendar, CLASS_CG_HEADER_LABEL, ENABLED);
        VBox schedule = builder.buildVBox(CG_CARD, scheduleBox, CLASS_CG_CARD, ENABLED);
        HBox itemsBox = builder.buildHBox(this, schedule, EMPTY_TEXT, ENABLED);
        builder.buildIconButton(CG_LESS_BUTTON, itemsBox, CLASS_CG_LESS_BUTTON, ENABLED);
        builder.buildLabel(CG_SCHEDULE_LABEL, itemsBox, CLASS_CG_HEADER_LABEL, ENABLED);
        VBox add = builder.buildVBox(CG_CARD, scheduleBox, CLASS_CG_CARD, ENABLED);
        builder.buildLabel(CG_ADD_LABEL, add, CLASS_CG_HEADER_LABEL, ENABLED);
        add.setSpacing(10);
        //Calendar
        HBox calendarBox = builder.buildHBox(this, calendar, EMPTY_TEXT, ENABLED);
        calendarBox.setSpacing(40);
        Label monLabel = builder.buildLabel(CG_MONDAY_LABEL, calendarBox, EMPTY_TEXT, ENABLED);
        DatePicker monPicker = new DatePicker();
        calendarBox.getChildren().add(monPicker);
        Label friLabel = builder.buildLabel(CG_FRIDAY_LABEL, calendarBox, EMPTY_TEXT, ENABLED);
        DatePicker friPicker = new DatePicker();
        calendarBox.getChildren().add(friPicker);

        //Schedule Table
        TableView<TeachingAssistantPrototype> scheduleTable = builder.buildTableView(CG_SCHEDULE_TABLE_VIEW, schedule, CLASS_CG_TABLE_VIEW, ENABLED);
        scheduleTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        TableColumn typeColumn = builder.buildTableColumn(CG_TYPE_TABLE_COLUMN, scheduleTable, CLASS_CG_COLUMN);
        TableColumn dateColumn = builder.buildTableColumn(CG_DATE_TABLE_COLUMN, scheduleTable, CLASS_CG_COLUMN);
        TableColumn titleColumn = builder.buildTableColumn(CG_TITLE_TABLE_COLUMN, scheduleTable, CLASS_CG_CENTERED_COLUMN);
        TableColumn topicColumn = builder.buildTableColumn(CG_TOPIC_TABLE_COLUMN, scheduleTable, CLASS_CG_COLUMN);
        typeColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<String, String>("email"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<String, String>("slots"));
        topicColumn.setCellValueFactory(new PropertyValueFactory<String, String>("type"));
        typeColumn.prefWidthProperty().bind(scheduleTable.widthProperty().multiply(1.0 / 5.0));
        dateColumn.prefWidthProperty().bind(scheduleTable.widthProperty().multiply(2.0 / 5.0));
        titleColumn.prefWidthProperty().bind(scheduleTable.widthProperty().multiply(1.0 / 5.0));
        topicColumn.prefWidthProperty().bind(scheduleTable.widthProperty().multiply(1.0 / 5.0));

        //Add/Edit Table 
        HBox rowOne = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        HBox rowTwo = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        HBox rowThree = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        HBox rowFour = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        HBox rowFive = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        HBox rowSix = builder.buildHBox(this, add, EMPTY_TEXT, ENABLED);
        rowOne.setSpacing(25);
        rowTwo.setSpacing(25);
        rowThree.setSpacing(28);
        rowFour.setSpacing(20);
        rowFive.setSpacing(30);
        rowSix.setSpacing(50);
        Label typeLabel = builder.buildLabel(CG_TYPE_LABEL, rowOne, EMPTY_TEXT, ENABLED);
        ComboBox typeBox = builder.buildComboBox(CG_TYPE_COMBOBOX, BUTTON_TAG_WIDTH, CLASS_CG_PROMPT, rowOne, EMPTY_TEXT, ENABLED);
        Label dateLabel = builder.buildLabel(CG_DATE_LABEL, rowTwo, EMPTY_TEXT, ENABLED);
        DatePicker datePicker = new DatePicker();
        rowTwo.getChildren().add(datePicker);
        Label titleLabel = builder.buildLabel(CG_TITLE_LABEL, rowThree, EMPTY_TEXT, ENABLED);
        TextField titleField = builder.buildTextField(CG_TITLE_TEXT_FIELD, rowThree, EMPTY_TEXT, ENABLED);
        Label topicLabel = builder.buildLabel(CG_TOPIC_LABEL, rowFour, EMPTY_TEXT, ENABLED);
        TextField topicField = builder.buildTextField(CG_TOPIC_TEXT_FIELD, rowFour, EMPTY_TEXT, ENABLED);
        Label linkLabel = builder.buildLabel(CG_LINK_LABEL, rowFive, EMPTY_TEXT, ENABLED);
        TextField linkField = builder.buildTextField(CG_LINK_TEXT_FIELD, rowFive, EMPTY_TEXT, ENABLED);
        Button updateButton = builder.buildTextButton(CG_UPDATE_BUTTON, rowSix, EMPTY_TEXT, ENABLED);
        Button clearButton = builder.buildTextButton(CG_CLEAR_BUTTON, rowSix, EMPTY_TEXT, ENABLED);

        scheduleTab.setContent(scrollPane);
    }

    private void setupOfficeHoursColumn(Object columnId, TableView tableView, String styleClass, String columnDataProperty) {
        AppNodesBuilder builder = app.getGUIModule().getNodesBuilder();
        TableColumn<TeachingAssistantPrototype, String> column = builder.buildTableColumn(columnId, tableView, styleClass);
        column.setCellValueFactory(new PropertyValueFactory<TeachingAssistantPrototype, String>(columnDataProperty));
        column.prefWidthProperty().bind(tableView.widthProperty().multiply(1.0 / 7.0));
        column.setCellFactory(col -> {
            return new TableCell<TeachingAssistantPrototype, String>() {
                @Override
                protected void updateItem(String text, boolean empty) {
                    super.updateItem(text, empty);
                    if (text == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        // CHECK TO SEE IF text CONTAINS THE NAME OF
                        // THE CURRENTLY SELECTED TA
                        setText(text);
                        TableView<TeachingAssistantPrototype> tasTableView = (TableView) app.getGUIModule().getGUINode(CG_TAS_TABLE_VIEW);
                        TeachingAssistantPrototype selectedTA = tasTableView.getSelectionModel().getSelectedItem();
                        if (selectedTA == null) {
                            setStyle("");
                        } else if (text.contains(selectedTA.getName())) {
                            setStyle("-fx-background-color: yellow");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });
    }

    public void initControllers() {
        CGController controller = new CGController((CGApp) app);
        AppGUIModule gui = app.getGUIModule();
//        AppFileController files = (AppFileController) app.getFileComponent();
//        Button favButton = (Button) gui.getGUINode(CG_FAV_BUTTON);
//        favButton.setOnAction(e->{
//            files.promptToOpen();
//        });

        
        
        
        
        ComboBox startTime = (ComboBox) gui.getGUINode(CG_START_TIME_COMBOBOX);
        ComboBox endTime = (ComboBox) gui.getGUINode(CG_END_TIME_COMBOBOX);
        startTime.setOnAction(e -> {
            endTime.setOnAction(x -> {
                controller.processTimeRange();
            });
        });
        Button ohButton = (Button) gui.getGUINode(CG_OH_BUTTON);
        TextField ohText = (TextField) gui.getGUINode(CG_OH_TEXT_FIELD);
        ohButton.setOnAction(e->{
            boolean set = ohText.visibleProperty().get();
            ohText.visibleProperty().set(!set);
        });
        
        // FOOLPROOF DESIGN STUFF
        TextField nameTextField = ((TextField) gui.getGUINode(CG_NAME_TEXT_FIELD));
        TextField emailTextField = ((TextField) gui.getGUINode(CG_EMAIL_TEXT_FIELD));

        nameTextField.textProperty().addListener(e -> {
            controller.processTypeTA();
        });
        emailTextField.textProperty().addListener(e -> {
            controller.processTypeTA();
        });

        //REMOVE TA
        Button removeTAButton = (Button) gui.getGUINode(CG_TAS_LESS_BUTTON);
        TableView tasTableView = (TableView) gui.getGUINode(CG_TAS_TABLE_VIEW);

        // FIRE THE ADD EVENT ACTION
        nameTextField.setOnAction(e -> {
            controller.processAddTA();
        });
        emailTextField.setOnAction(e -> {
            controller.processAddTA();
        });
        ((Button) gui.getGUINode(CG_ADD_TA_BUTTON)).setOnAction(e -> {
            controller.processAddTA();
        });

        TableView officeHoursTableView = (TableView) gui.getGUINode(CG_OFFICE_HOURS_TABLE_VIEW);
        officeHoursTableView.getSelectionModel().setCellSelectionEnabled(true);
        officeHoursTableView.setOnMouseClicked(e -> {
            controller.processToggleOfficeHours();
        });

        // DON'T LET ANYONE SORT THE TABLES
        for (int i = 0; i < officeHoursTableView.getColumns().size(); i++) {
            ((TableColumn) officeHoursTableView.getColumns().get(i)).setSortable(false);
        }
        for (int i = 0; i < tasTableView.getColumns().size(); i++) {
            ((TableColumn) tasTableView.getColumns().get(i)).setSortable(false);
        }

        tasTableView.setOnMouseClicked(e -> {
            app.getFoolproofModule().updateAll();
            if (e.getClickCount() == 2) {
                controller.processEditTA();
            }
            controller.processSelectTA();
            removeTAButton.setOnAction(x -> {

                controller.processRemoveTA();

            });
        });

        RadioButton allRadio = (RadioButton) gui.getGUINode(CG_ALL_RADIO_BUTTON);
        allRadio.setOnAction(e -> {
            controller.processSelectAllTAs();
        });
        RadioButton gradRadio = (RadioButton) gui.getGUINode(CG_GRAD_RADIO_BUTTON);
        gradRadio.setOnAction(e -> {
            controller.processSelectGradTAs();
        });
        RadioButton undergradRadio = (RadioButton) gui.getGUINode(CG_UNDERGRAD_RADIO_BUTTON);
        undergradRadio.setOnAction(e -> {
            controller.processSelectUndergradTAs();
        });
    }

    public void initFoolproofDesign() {
        AppGUIModule gui = app.getGUIModule();
        AppFoolproofModule foolproofSettings = app.getFoolproofModule();
        foolproofSettings.registerModeSettings(CG_FOOLPROOF_SETTINGS,
                new CGFoolproofDesign((CGApp) app));
    }

    @Override
    public void processWorkspaceKeyEvent(KeyEvent ke) {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }

    @Override
    public void showNewDialog() {
        // WE AREN'T USING THIS FOR THIS APPLICATION
    }
}
