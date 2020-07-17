package Controller;

import Model.MVC.AcModel;
import Util.Dictionary.ACDictionary;
import Util.Dictionary.BasicInfoDictionary;
import Util.Dictionary.IDictionary;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CreateAccountController {

    @FXML private Label firstNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label userNameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confPasswordLabel;
    @FXML private Label passwodHintLabel;

    public static boolean isFirstAccount = false;
    @FXML private ComboBox<String> admins;
    @FXML private TextField passwordHint;
    @FXML private TextField email;
    @FXML private Label checkValidityPassword;

    @FXML private Label checkValidityUserName;

    @FXML private Label checkPassword;

    @FXML private Label checkValidityEmail;
    @FXML private TextField firstNameTextField;

    @FXML private TextField lastNameTextField;

    @FXML private PasswordField newPasswordField;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField userNameTextField;
    IDictionary basicInfoDic;
    IDictionary acDic;
    private AcModel model;
    private Pane root;
    Stage stage;
    private Scene viewCreateAccount;
    @FXML Button create;

    public CreateAccountController() throws IOException {
        this.stage = new Stage();
        this.model = new AcModel();
        SetDictionary();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/adminAccount.fxml"));
        loader.setController(this);
        root = (Pane) loader.load();
        setLabels();
    }

    private void setLabels() {
        userNameLabel.setText(IDictionary.getTranslation(acDic, "Username"));
        passwordLabel.setText(IDictionary.getTranslation(acDic, "Password"));
        firstNameLabel.setText(IDictionary.getTranslation(acDic, "First name"));
        lastNameLabel.setText(IDictionary.getTranslation(acDic, "Last name"));
        confPasswordLabel.setText(IDictionary.getTranslation(acDic, "Confirm Password"));
        passwodHintLabel.setText(IDictionary.getTranslation(acDic, "Password Hint"));
        create.setText(IDictionary.getTranslation(acDic, "Login"));
    }

    private void SetDictionary() {
        acDic = new ACDictionary();
        basicInfoDic = new BasicInfoDictionary();
    }

    public Pane getPane() {
        return root;
    }

    public CreateAccountController(Stage stage, AcModel model) throws IOException {
        SetDictionary();
        this.stage = stage;
        this.model = model;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/adminAccount.fxml"));
        loader.setController(this);
        viewCreateAccount = new Scene(loader.load());
        stage.setTitle(IDictionary.getTranslation(acDic, "Create Admin Account"));
        stage.setScene(viewCreateAccount);
        stage.setResizable(false);
        stage.getIcons()
                .add(
                        new Image(
                                getClass()
                                        .getResourceAsStream(
                                                "/Style/Logo/Logo-idea-2-blackbg--logo.png")));
        setLabels();
        stage.show();
    }

    @FXML
    public void createNewAccount() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(IDictionary.getTranslation(acDic, "Create Account Error"));
        alert.setHeaderText(
                IDictionary.getTranslation(acDic, "Create Account was not possible due to:"));
        if (onCheckNewUserName(userNameTextField.getText())) {
            if (onCheckEmail(email.getText())) {
                if (onCheckConfirmPassword(
                        newPasswordField.getText(), confirmPasswordField.getText())) {
                    if (!passwordHint.getText().contains(newPasswordField.getText())
                            && passwordHint.getText() != null
                            && passwordHint.getText().length() > 0) {
                        if (!handleCreateAccountOverToModel()) {
                            alert.setContentText(
                                    IDictionary.getTranslation(
                                            acDic, "A password must be at least 4 characters!"));
                            alert.showAndWait();
                        }
                        // back
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle(IDictionary.getTranslation(acDic, "Create new Account!"));
                        alert1.setContentText(
                                IDictionary.getTranslation(
                                        acDic, "Your Account has been created!"));
                        alert1.showAndWait();
                        if (isFirstAccount) onShowView();
                    } else {
                        alert.setContentText(
                                IDictionary.getTranslation(
                                        acDic,
                                        "Your password hint must not contain your password & not empty!"));
                        alert.showAndWait();
                    }
                } else {
                    alert.setContentText(
                            IDictionary.getTranslation(acDic, "Incongruent passwords!"));
                    alert.showAndWait();
                }
            } else {
                alert.setContentText(
                        IDictionary.getTranslation(acDic, "Your Email must contain @ and .!"));
                alert.showAndWait();
            }
        } else {
            alert.setContentText(
                    IDictionary.getTranslation(
                            acDic,
                            "Username already taken or forbidden characters used! A username must contain only alphanumeric characters."));
            alert.showAndWait();
        }
    }

    public void onShowView() {
        try {
            new AcController(stage, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean handleCreateAccountOverToModel() {
        if (AcModel.isPasswordValid(newPasswordField.getText())) {
            model.createNewAccount(
                    userNameTextField.getText(),
                    newPasswordField.getText(),
                    firstNameTextField.getText(),
                    lastNameTextField.getText(),
                    passwordHint.getText(),
                    email.getText());
            return true;
        }
        return false;
    }

    @FXML
    public boolean onCheckNewUserName() {
        return onCheckNewUserName(userNameTextField.getText());
    }

    private boolean onCheckNewUserName(String newUserName) {
        if (newUserName == null || newUserName.length() == 0) {
            checkValidityUserName.setText("X");
            return false;
        }
        if (model.isUserNameValid(newUserName)) {
            checkValidityUserName.setText("✔");
            return true;
        }
        checkValidityUserName.setText("X");
        return false;
    }

    @FXML
    public boolean onCheckPasswordComplexity() {
        return onCheckPasswordComplexity(newPasswordField.getText());
    }

    private boolean onCheckPasswordComplexity(String password) {
        if (password != null && password.length() > 0) {
            if (AcModel.isPasswordValid(password)) {
                checkPassword.setText("✔");
                return true;
            }
        }
        checkPassword.setText("X");
        return false;
    }

    @FXML
    public boolean onCheckEmail() {
        return onCheckEmail(email.getText());
    }

    private boolean onCheckEmail(String email) {
        if (email == null || email.length() == 0) {
            checkValidityEmail.setText("X");
            return false;
        }
        if (email.contains("@") && email.contains(".")) {
            checkValidityEmail.setText("✔");
            return true;
        }
        checkValidityEmail.setText("X");
        return false;
    }

    @FXML
    public boolean onCheckConfirmPassword() {
        return onCheckConfirmPassword(newPasswordField.getText(), confirmPasswordField.getText());
    }

    private boolean onCheckConfirmPassword(String newPassword, String confirmPassword) {
        if (newPassword == null
                || newPassword.length() == 0
                || confirmPassword == null
                || confirmPassword.length() == 0) {
            checkValidityPassword.setText("X");
            return false;
        }
        if (newPassword.equals(confirmPassword)) {
            checkValidityPassword.setText("✔");
            return true;
        }
        checkValidityPassword.setText("X");
        return false;
    }
}
