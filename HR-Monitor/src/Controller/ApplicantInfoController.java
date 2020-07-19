package Controller;

import static Database.Method.getImageById;
import static Model.Status.*;

import Database.Connector;
import Model.*;
import Model.Image.AppImage;
import Model.Image.ImageType;
import Model.MVC.OverviewModel;
import Model.User.ApplicantUI;
import Util.Dictionary.*;
import Util.ImageTools;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class ApplicantInfoController {
    private boolean isHRChartLoaded = false;
    Stage stage;
    OverviewModel model;
    ApplicantUI app;
    Scene scene;
    // Create a TableView with a list of Education Info of an Applicant
    @FXML TableView<Education> eduInfoTblFX;
    private ObservableList<Education> observableListEduInfoTableView;

    @FXML TableView<WorkExperience> workInfoTblFX;
    private ObservableList<WorkExperience> observableListWorkExpInfoTableView;

    @FXML TableView<String> posTable;
    private ObservableList<String> observableListPosTableTableView;

    @FXML TableView<String> indTable;
    private ObservableList<String> observableListIndTableTableView;

    @FXML TableView<String> pLnFWTableFX;
    private ObservableList<String> observableListPLnFWTableView;

    @FXML TableView<String> bSkillsTableFX;
    private ObservableList<String> observableListBSkillsTableView;

    @FXML TableView<String> dBTableFX;
    private ObservableList<String> observableListDBTableView;

    @FXML TableView<String> proSoftTableFX;
    private ObservableList<String> observableListProSoftTableView;

    @FXML TableView<String> spoLanTableFX;
    private ObservableList<String> observableListSpoLanTableView;

    @FXML TableColumn<String, String> posFX = new TableColumn<>("Position");
    @FXML TableColumn<String, String> indFX = new TableColumn<>("Industry");

    // Applicant Info View's Variables
    // 1. Basic Info
    @FXML TextField lblFNameFX, lblLNameFX, lblEmailFX, lblPNumberFX, lblGenderFX1;
    @FXML Hyperlink hplLinkedInFX, hplXingFX;
    // 2. Edu Info
    @FXML TableColumn<Education, String> universityFX = new TableColumn<>("university");
    @FXML TableColumn<Education, String> subjectFX = new TableColumn<>("subject");
    @FXML TableColumn<Education, String> degreeFX = new TableColumn<>("degree");
    @FXML TableColumn<Education, Number> gradeFX = new TableColumn<>("grade");
    @FXML TableColumn<Education, String> gradYearFX = new TableColumn<>("date");

    // 2. work Info
    @FXML TableColumn<WorkExperience, String> jobTitleFX = new TableColumn<>("jobTitle");
    @FXML TableColumn<WorkExperience, String> companyFX = new TableColumn<>("company");

    @FXML
    TableColumn<WorkExperience, String> employmentTypeFX = new TableColumn<>("employmentType");

    @FXML TableColumn<WorkExperience, String> startDateFX = new TableColumn<>("startDate");
    @FXML TableColumn<WorkExperience, String> endDateFX = new TableColumn<>("endDate");
    @FXML TableColumn<WorkExperience, String> descriptionFX = new TableColumn<>("description");

    // 3. Image of the Applicant

    // Additional Info
    @FXML Label lblAddInfo;
    @FXML private ImageView imgFX;
    @FXML StackPane imgstckPFX;

    // Documents tab
    @FXML ScrollPane documentsGridFX;
    @FXML Tab docTabFX;

    // Key Competencies
    @FXML TableColumn<String, String> pLnFWColFX = new TableColumn<>("name");
    @FXML TableColumn<String, String> bSkillsColFX = new TableColumn<>("name");
    @FXML TableColumn<String, String> dBColFX = new TableColumn<>("name");
    @FXML TableColumn<String, String> proSoftColFX = new TableColumn<>("name");
    @FXML TableColumn<String, String> spoLanColFX = new TableColumn<>("name");

    @FXML Label txtrheFX, txtMotFX, txtSelfFX, txtPerFX;
    @FXML TextField txtImpFX, txtImpHRFX;

    // Change status
    @FXML Label btnOFX, btnHRFX, btnDFX;
    @FXML Label lblStatusFX;

    // Bar chart - HR Ratings
    @FXML HBox hBoxBChartFX;

    // Save
    @FXML Button btnSaveFX;
    IDictionary KCDic;
    IDictionary DEDic;
    IDictionary PIDic;

    //// For translation - Headers
    // Titled Panes
    @FXML TitledPane titleBasicInfoFX;
    @FXML TitledPane titleAddInfoFX;
    @FXML TitledPane titleWExperienceFX;
    @FXML TitledPane titleEduInfoFX;
    @FXML TitledPane titleKCompFX;
    @FXML TitledPane titleFoInterestFX;
    @FXML TitledPane commentFTMem;
    @FXML TitledPane commentHRMem;
    // Basic Info
    @FXML Label fNameFX;
    @FXML Label lNameFX;
    @FXML Label genderFX1;
    @FXML Label phoneNumeberFX;
    // Tabs
    @FXML Tab infoTabFX;
    @FXML Tab appRatingTabFX;

    @FXML Label changeStatusFX;

    @FXML Label successLabelFX;

    IDictionary dictionary;

    public ApplicantInfoController(long id, OverviewModel model) {
        this.model = model;
        app = this.model.getApplicantByID(id);
        setDictionary();
        showApplicantInfo();
    }

    private void setDictionary() {
        KCDic = new KeyCompetenciesDictionary();
        DEDic = new DegreeAndEmploymentTypeDictionary();
        PIDic = new PositionsAndIndustriesDictionary();
        dictionary = new ApplicantInfoDictionary();
    }

    public void showApplicantInfo() {
        try {
            createAndSetNewStage();
            setApplicantInfo();

        } catch (IOException e) {
            System.err.println("unable to load Applicant!");
            e.printStackTrace();
        }
    }

    private void setApplicantInfo() {
        setHeaders();
        getTableBasicInfo();
        getPositionAndIndustry();
        getTableEduInfo();
        getTableWorkExpInfo();
        getImages(ImageType.profilePic);
        getKeyCompetence();
        getStatus();
        getHrRating();
    }

    private void setHeaders() {
        titleBasicInfoFX.setText(IDictionary.getTranslation(dictionary, "Basic Information"));
        titleAddInfoFX.setText(IDictionary.getTranslation(dictionary, "Additional Information"));
        titleWExperienceFX.setText(IDictionary.getTranslation(dictionary, "Work Experience"));
        titleEduInfoFX.setText(IDictionary.getTranslation(dictionary, "Educational Information"));
        titleKCompFX.setText(IDictionary.getTranslation(dictionary, "Key Competencies"));
        titleFoInterestFX.setText(IDictionary.getTranslation(dictionary, "Fields of Interest"));

        jobTitleFX.setText(IDictionary.getTranslation(dictionary, "Title"));
        companyFX.setText(IDictionary.getTranslation(dictionary, "Company"));
        employmentTypeFX.setText(IDictionary.getTranslation(dictionary, "Employment Type"));
        startDateFX.setText(IDictionary.getTranslation(dictionary, "Start Date"));
        endDateFX.setText(IDictionary.getTranslation(dictionary, "End Date"));
        descriptionFX.setText(IDictionary.getTranslation(dictionary, "Description"));

        universityFX.setText(IDictionary.getTranslation(dictionary, "University"));
        subjectFX.setText(IDictionary.getTranslation(dictionary, "Subject"));
        degreeFX.setText(IDictionary.getTranslation(dictionary, "Degree"));
        gradeFX.setText(IDictionary.getTranslation(dictionary, "Grade"));
        gradYearFX.setText(IDictionary.getTranslation(dictionary, "Graduation Year"));

        pLnFWColFX.setText(IDictionary.getTranslation(dictionary, "Programming Languages"));
        bSkillsColFX.setText(IDictionary.getTranslation(dictionary, "Bussiness Skills"));
        dBColFX.setText(IDictionary.getTranslation(dictionary, "Databases"));
        proSoftColFX.setText(IDictionary.getTranslation(dictionary, "Professional Software"));
        spoLanColFX.setText(IDictionary.getTranslation(dictionary, "Spoken Langauges"));

        posFX.setText(IDictionary.getTranslation(dictionary, "Position"));
        indFX.setText(IDictionary.getTranslation(dictionary, "Industry"));

        genderFX1.setText(IDictionary.getTranslation(dictionary, "Salutation"));
        fNameFX.setText(IDictionary.getTranslation(dictionary, "First Name"));
        lNameFX.setText(IDictionary.getTranslation(dictionary, "Second Name"));
        phoneNumeberFX.setText(IDictionary.getTranslation(dictionary, "Phone Number"));

        infoTabFX.setText(IDictionary.getTranslation(dictionary, "Information"));
        docTabFX.setText(IDictionary.getTranslation(dictionary, "Documents"));
        appRatingTabFX.setText(IDictionary.getTranslation(dictionary, "Applicant Ratings"));
    }

    private void getBarChart() {
        if (!isHRChartLoaded) {

            CategoryAxis yAxis = new CategoryAxis();
            NumberAxis xAxis = new NumberAxis();
            BarChart<Number, String> barChartFX = new BarChart<Number, String>(xAxis, yAxis);
            xAxis.upperBoundProperty().setValue(5);
            xAxis.setLabel(IDictionary.getTranslation(dictionary, "Rating"));
            yAxis.setLabel(IDictionary.getTranslation(dictionary, "Categories"));
            yAxis.setStyle("-fx-tick-label-fill:black");
            xAxis.setStyle("-fx-tick-label-fill:black");
            barChartFX.setStyle("-fx-tick-label-fill:black");
            barChartFX.setLegendVisible(false);
            barChartFX.setMaxHeight(5000);
            XYChart.Series series = new XYChart.Series();

            series.getData()
                    .add(
                            new XYChart.Data(
                                    app.getHrRating().getRhetoric(),
                                    IDictionary.getTranslation(dictionary, "Rhetoric")));
            series.getData()
                    .add(
                            new XYChart.Data(
                                    app.getHrRating().getMotivation(),
                                    IDictionary.getTranslation(dictionary, "Motivation")));
            series.getData()
                    .add(
                            new XYChart.Data(
                                    app.getHrRating().getPersonalImpression(),
                                    IDictionary.getTranslation(dictionary, "Personal Impression")));
            series.getData()
                    .add(
                            new XYChart.Data(
                                    app.getHrRating().getSelfAssurance(),
                                    IDictionary.getTranslation(dictionary, "Self Assurance")));

            barChartFX.getData().add(series);
            barChartFX.setTitle(IDictionary.getTranslation(dictionary, "HR Ratings"));
            hBoxBChartFX.getChildren().add(barChartFX);
        }
        isHRChartLoaded = true;
    }

    @FXML
    private void onShowDocuments() {
        getImages(ImageType.CV);
    }

    @FXML
    private void onShowRating() {
        statusListener();
        getBarChart();
        setHeader4AppRating();
    }

    private void setHeader4AppRating() {
        changeStatusFX.setText(IDictionary.getTranslation(dictionary, "Change status"));
        btnOFX.setText(IDictionary.getTranslation(dictionary, "Open"));
        btnHRFX.setText(IDictionary.getTranslation(dictionary, "Sent to HR"));
        btnDFX.setText(IDictionary.getTranslation(dictionary, "Denied"));

        commentFTMem.setText(
                IDictionary.getTranslation(dictionary, "Comment by the fair team member"));
        commentHRMem.setText(IDictionary.getTranslation(dictionary, "Comment by the HR member"));
        txtImpHRFX.setPromptText(
                IDictionary.getTranslation(dictionary, "Add a comment about the Applicant"));

        btnSaveFX.setText(IDictionary.getTranslation(dictionary, "Save Changes"));
    }

    private void getStatus() {
        setStatusLabel(app.getStatus());
        statusListener();
    }

    private void notification(String successMessage) {
        successLabelFX.setVisible(true);
        if (successMessage != null && successMessage.length() != 0) {
            successLabelFX.setText(
                    IDictionary.getTranslation(dictionary, "Changes saved succesfully!"));
        } else {
            successLabelFX.setText(
                    IDictionary.getTranslation(dictionary, "Couldn't save! Connection error."));
        }
        PauseTransition visiblePause = new PauseTransition();
        visiblePause.setDuration(javafx.util.Duration.seconds(2));
        visiblePause.setOnFinished(event -> successLabelFX.setVisible(false));
        visiblePause.play();
    }

    private void setStatusLabel(Status status) {
        switch (status) {
            case Open:
                lblStatusFX.setStyle(
                        "-fx-background-color: #61d0ee; -fx-background-radius:10 10 10 10");
                lblStatusFX.setText(IDictionary.getTranslation(dictionary, "Open"));
                lblStatusFX.minWidth(80);
                break;
            case Send2HR:
                lblStatusFX.setStyle(
                        "-fx-background-color: #5be14f; -fx-background-radius:10 10 10 10");
                lblStatusFX.setText(IDictionary.getTranslation(dictionary, "Sent to HR"));
                lblStatusFX.minWidth(110);
                break;
            case Denied:
                lblStatusFX.setStyle(
                        "-fx-background-color: #ff927e; -fx-background-radius:10 10 10 10");
                lblStatusFX.setText(IDictionary.getTranslation(dictionary, "Denied"));
                lblStatusFX.minWidth(85);
                break;
        }
    }

    public void statusListener() {
        btnOFX.setOnMouseClicked(
                (event) -> {
                    setStatusLabel(Open);
                    String s = Connector.changeStatus(app.getID(), Open);
                    app.setStatus(Open);
                    notification(s);
                });
        btnHRFX.setOnMouseClicked(
                (event) -> {
                    setStatusLabel(Send2HR);
                    String s = Connector.changeStatus(app.getID(), Send2HR);
                    app.setStatus(Send2HR);
                    notification(s);
                });
        btnDFX.setOnMouseClicked(
                (event) -> {
                    setStatusLabel(Denied);
                    String s = Connector.changeStatus(app.getID(), Denied);
                    app.setStatus(Denied);
                    notification(s);
                });
    }

    private void createAndSetNewStage() throws IOException {
        Stage stageApplicantInfo = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/applicantInfo.fxml"));
        loader.setController(this);
        scene = new Scene(loader.load());
        stageApplicantInfo.show();
        stageApplicantInfo.setScene(scene);
        stageApplicantInfo.setTitle(
                IDictionary.getTranslation(dictionary, "Applicant: ")
                        + app.getFirstName()
                        + " "
                        + app.getLastName());
        stageApplicantInfo
                .getIcons()
                .add(
                        new Image(
                                getClass()
                                        .getResourceAsStream(
                                                "/Style/Logo/Logo-idea-2-blackbg--logo.png")));
    }

    private void getHrRating() {
        txtImpFX.setText(app.getHrRating().getImpression());
        txtImpHRFX.setOnKeyReleased(
                event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        Connector.postHRComment(app.getID(), txtImpHRFX.getText());
                        app.setHrComment(txtImpHRFX.getText());
                    }
                });
        txtImpHRFX.setText(app.getHrComment());
        btnSaveFX.setOnMouseClicked(
                mouseEvent -> {
                    String s = Connector.postHRComment(app.getID(), txtImpHRFX.getText());
                    app.setHrComment(txtImpHRFX.getText());
                    notification(s);
                });
    }

    private void getKeyCompetence() {
        observableListPLnFWTableView = pLnFWTableFX.getItems();
        observableListBSkillsTableView = bSkillsTableFX.getItems();
        observableListDBTableView = dBTableFX.getItems();
        observableListProSoftTableView = proSoftTableFX.getItems();
        observableListSpoLanTableView = spoLanTableFX.getItems();

        observableListPLnFWTableView.clear();
        observableListBSkillsTableView.clear();
        observableListDBTableView.clear();
        observableListProSoftTableView.clear();
        observableListSpoLanTableView.clear();
        pLnFWColFX.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().toString()));
        bSkillsColFX.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().toString()));
        dBColFX.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().toString()));
        proSoftColFX.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().toString()));
        spoLanColFX.setCellValueFactory(x -> new ReadOnlyStringWrapper(x.getValue().toString()));

        observableListPLnFWTableView.setAll(
                app.getKeyCompetencies(KeyCompetenciesCategory.ProgrammingLanguagesAndFrameworks)
                        .stream()
                        .map(
                                x ->
                                        IDictionary.getTranslation(KCDic, x.getName())
                                                + " - "
                                                + x.getRating())
                        .collect(Collectors.toList()));
        observableListBSkillsTableView.setAll(
                app.getKeyCompetencies(KeyCompetenciesCategory.BusinessSkills)
                        .stream()
                        .map(
                                x ->
                                        IDictionary.getTranslation(KCDic, x.getName())
                                                + " - "
                                                + x.getRating())
                        .collect(Collectors.toList()));
        observableListDBTableView.setAll(
                app.getKeyCompetencies(KeyCompetenciesCategory.Databases)
                        .stream()
                        .map(
                                x ->
                                        IDictionary.getTranslation(KCDic, x.getName())
                                                + " - "
                                                + x.getRating())
                        .collect(Collectors.toList()));
        observableListProSoftTableView.setAll(
                app.getKeyCompetencies(KeyCompetenciesCategory.ProfessionalSoftware)
                        .stream()
                        .map(
                                x ->
                                        IDictionary.getTranslation(KCDic, x.getName())
                                                + " - "
                                                + x.getRating())
                        .collect(Collectors.toList()));
        observableListSpoLanTableView.setAll(
                app.getKeyCompetencies(KeyCompetenciesCategory.Languages)
                        .stream()
                        .map(
                                x ->
                                        IDictionary.getTranslation(KCDic, x.getName())
                                                + " - "
                                                + x.getRating())
                        .collect(Collectors.toList()));
    }

    private void getPositionAndIndustry() {
        if (app.getPositions() != null) getPositionTable(app.getPositions());
        if (app.getIndustries() != null) getIndTable(app.getIndustries());
    }

    private ObservableList<String> getIndTable(List<Industries> industries) {
        observableListIndTableTableView = indTable.getItems();
        observableListIndTableTableView.clear();
        observableListIndTableTableView.addAll(
                industries
                        .stream()
                        .map(x -> IDictionary.getTranslation(PIDic, x.getIndustry().toString()))
                        .collect(Collectors.toList()));
        setFactoriesAndComparatorsForIndTableColumns();
        return observableListIndTableTableView;
    }

    public void setFactoriesAndComparatorsForIndTableColumns() {
        indFX.setCellValueFactory(ind -> new ReadOnlyStringWrapper(ind.getValue()));
    }

    private ObservableList<String> getPositionTable(List<Positions> positions) {
        observableListPosTableTableView = posTable.getItems();
        observableListPosTableTableView.clear();
        observableListPosTableTableView.addAll(
                positions
                        .stream()
                        .map(x -> IDictionary.getTranslation(PIDic, x.getPosition().toString()))
                        .collect(Collectors.toList()));
        setFactoriesAndComparatorsForPosTableColumns();
        return observableListPosTableTableView;
    }

    public void setFactoriesAndComparatorsForPosTableColumns() {
        posFX.setCellValueFactory(pos -> new ReadOnlyStringWrapper(pos.getValue()));
    }

    private void getImages(ImageType it) {
        List<AppImage> images = app.getAppImage();
        if (it.equals(ImageType.profilePic)) {
            setProfPic(images);
        } else {
            List<AppImage> docImgs =
                    images.stream()
                            .sequential()
                            .filter(x -> !x.getType().equals(ImageType.profilePic))
                            .collect(Collectors.toList());
            if (docImgs.size() > 0) setDocumentsImage(docImgs);
        }
    }

    private void setProfPic(List<AppImage> images) {
        AppImage profImage;
        try {
            profImage =
                    images.stream()
                            .filter(x -> x.getType().equals(ImageType.profilePic))
                            .findFirst()
                            .get();
        } catch (Exception e) {
            profImage = null;
        }

        if (profImage != null) {
            try {
                if (profImage.getContent() == null)
                    Connector.sendGetHttp(
                            getImageById, String.valueOf(app.getID()), profImage.getId());
                ImageTools.parseImageStringToImage(profImage);
                File file = new File(profImage.getPath());
                imgFX.setImage(SwingFXUtils.toFXImage(ImageIO.read(file), null));
                imgFX.fitWidthProperty().bind(imgstckPFX.widthProperty());
                imgFX.fitHeightProperty().bind(imgstckPFX.heightProperty());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setDocumentsImage(List<AppImage> imageList) {
        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER);
        hb.setPadding(new Insets(25, 25, 25, 25));
        hb.setSpacing(50);
        documentsGridFX.fitToHeightProperty();
        try {
            for (AppImage appImage : imageList) {
                ImageView imageView = new ImageView();
                if (appImage.getContent() == null)
                    Connector.sendGetHttp(
                            getImageById, String.valueOf(app.getID()), appImage.getId());
                ImageTools.parseImageStringToImage(appImage);
                File file = new File(appImage.getPath());
                setImageConfig(imageView, file);
                hb.getChildren().add(imageView);
            }
        } catch (IOException e) {
            System.err.println("Cannot load Documents!");
        }
        documentsGridFX.setContent(hb);
    }

    private void setImageConfig(ImageView imageView, File file) throws IOException {
        imageView.minWidth(-1);
        imageView.setFitHeight(documentsGridFX.heightProperty().getValue());
        imageView.setPreserveRatio(true);
        imageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(file), null));
        addEventsToImage(imageView, file, documentsGridFX);
    }

    private void addEventsToImage(ImageView imageView, File file, ScrollPane documentsGridFX) {
        docClick(imageView, file);
        zoomImage(imageView, documentsGridFX);
        moveImage(imageView);
    }

    private void moveImage(ImageView imageView) {
        AtomicReference<Double> startDragX = new AtomicReference<>((double) 0);
        AtomicReference<Double> startDragY = new AtomicReference<>((double) 0);

        imageView.setOnMousePressed(
                e -> {
                    startDragX.set(e.getSceneX());
                    startDragY.set(e.getSceneY());
                });

        imageView.setOnMouseDragged(
                e -> {
                    imageView.setTranslateX(e.getSceneX() - startDragX.get());
                    imageView.setTranslateY(e.getSceneY() - startDragY.get());
                });
    }

    private void zoomImage(ImageView imageView, ScrollPane documentsGridFX) {
        final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
        zoomProperty.addListener(
                new InvalidationListener() {
                    @Override
                    public void invalidated(Observable arg0) {
                        imageView.setFitWidth(zoomProperty.get() * 4);
                        imageView.setFitHeight(zoomProperty.get() * 4);
                    }
                });

        documentsGridFX.addEventFilter(
                ScrollEvent.ANY,
                new EventHandler<ScrollEvent>() {
                    @Override
                    public void handle(ScrollEvent event) {
                        if (event.getDeltaY() > 0) {
                            zoomProperty.set(zoomProperty.get() * 1.1);
                        } else if (event.getDeltaY() < 0) {
                            zoomProperty.set(zoomProperty.get() / 1.1);
                        }
                    }
                });
    }

    private void docClick(ImageView imageView, File file) {
        imageView.setOnMouseClicked(
                (event) -> {
                    if (event.getClickCount() == 2) {
                        showDocImage(file);
                    }
                });
    }

    private void showDocImage(File imgFile) {
        Stage stageDocImage = new Stage();
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(imgFile), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(-1);
        imageView.setPreserveRatio(true);
        ScrollPane scrollPane = new ScrollPane();
        VBox vb = new VBox();
        vb.getChildren().add(imageView);
        vb.setAlignment(Pos.CENTER);
        vb.setFillWidth(true);
        vb.minWidth(-1);
        scrollPane.setContent(vb);
        Group root = new Group(scrollPane);
        Scene scene = new Scene(root);
        stageDocImage.show();
        stageDocImage.setTitle("Document");
        stageDocImage.setFullScreen(true);
        stageDocImage.setScene(scene);

        zoomImage(imageView, scrollPane);
        moveImage(imageView);
    }

    private void getTableBasicInfo() {
        lblGenderFX1.setText(IDictionary.getTranslation(dictionary, app.getTitle().toString()));
        lblFNameFX.setText(app.getFirstName());
        lblLNameFX.setText(app.getLastName());
        lblEmailFX.setText(app.getEmail());
        lblPNumberFX.setText(app.getPhone());
        lblAddInfo.setText(app.getAdditionalInfo());
        hplLinkedInFX.setText(app.getLinkedIn());
        hplXingFX.setText(app.getXing());
        profAccountLinkActions();
    }

    void profAccountLinkActions() {
        hplLinkedInFX.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        try {
                            java.awt.Desktop.getDesktop()
                                    .browse(
                                            URI.create(
                                                    "https://www.linkedin.com/in/"
                                                            + hplLinkedInFX.getText()));

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });
        hplXingFX.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        try {
                            java.awt.Desktop.getDesktop()
                                    .browse(
                                            URI.create(
                                                    "https://www.xing.com/profile/"
                                                            + hplXingFX.getText()));

                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                });
    }

    private ObservableList<Education> getTableEduInfo() {
        observableListEduInfoTableView = eduInfoTblFX.getItems();
        observableListEduInfoTableView.clear();
        observableListEduInfoTableView.addAll(app.getEducation());
        setFactoriesAndComparatorsForEduInfoTableColumns();
        return observableListEduInfoTableView;
    }

    private ObservableList<WorkExperience> getTableWorkExpInfo() {
        observableListWorkExpInfoTableView = workInfoTblFX.getItems();
        observableListWorkExpInfoTableView.clear();
        observableListWorkExpInfoTableView.addAll(app.getWorkExperience());
        setFactoriesAndComparatorsForWorkExpInfoTableColumns();
        return observableListWorkExpInfoTableView;
    }

    public void setFactoriesAndComparatorsForWorkExpInfoTableColumns() {
        jobTitleFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getJobTitle()));
        companyFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getCompany()));
        employmentTypeFX.setCellValueFactory(
                applicant ->
                        new ReadOnlyStringWrapper(
                                IDictionary.getTranslation(
                                        DEDic,
                                        applicant.getValue().getEmploymentType().toString())));
        startDateFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getStartDate()));
        endDateFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getEndDate()));
        descriptionFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getDescription()));
    }

    public void setFactoriesAndComparatorsForEduInfoTableColumns() {
        universityFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getUniversity()));
        subjectFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getSubject()));
        degreeFX.setCellValueFactory(
                applicant ->
                        new ReadOnlyStringWrapper(
                                IDictionary.getTranslation(
                                        DEDic, applicant.getValue().getDegree().toString())));
        gradeFX.setCellValueFactory(
                applicant -> new ReadOnlyDoubleWrapper(applicant.getValue().getGrade()));
        gradYearFX.setCellValueFactory(
                applicant -> new ReadOnlyStringWrapper(applicant.getValue().getGraduationYear()));
    }
}
