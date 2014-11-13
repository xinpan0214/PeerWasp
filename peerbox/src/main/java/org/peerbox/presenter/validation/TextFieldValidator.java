package org.peerbox.presenter.validation;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import org.peerbox.presenter.validation.ValidationUtils.ValidationResult;

public abstract class TextFieldValidator {

	protected TextField validateTxtField;
	protected StringProperty errorProperty;
	
	public TextFieldValidator(TextField txtField) {
		this(txtField, null);
	}
	
	public TextFieldValidator(TextField txtField, StringProperty errorProperty) {
		this.validateTxtField = txtField;
		this.errorProperty = errorProperty;
		initChangeListener();
	}
	
	private void initChangeListener() {
		validateTxtField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				validate(newValue);
			}
		});
	}
	
	public abstract ValidationResult validate(final String value);

	public TextField getTextField() {
		return validateTxtField;
	}
	
	protected void undecorateError() {
		validateTxtField.getStyleClass().removeAll("validation-error");
	}

	protected void decorateError() {
		if (!validateTxtField.getStyleClass().contains("validation-error")) {
			validateTxtField.getStyleClass().add("validation-error");
		}
	}
	
	public void setErrorProperty(StringProperty errorProperty) {
		this.errorProperty  = errorProperty;
	}
	
	public boolean hasErrorProperty() {
		return errorProperty != null;
	}
	
	protected void setErrorMessage(final String error) {
		if(hasErrorProperty()) {
			errorProperty.setValue(error);
		}
	}
	
	protected void clearErrorMessage() {
		setErrorMessage("");
	}
}
