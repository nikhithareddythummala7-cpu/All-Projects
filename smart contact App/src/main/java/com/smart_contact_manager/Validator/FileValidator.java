package com.smart_contact_manager.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final long MAX_FILE_SIZE = 1024 * 1024 * 2; // 2MB

    // We can also check type, height, width

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {

        if(file == null || file.isEmpty()){
//            constraintValidatorContext.disableDefaultConstraintViolation();
//            constraintValidatorContext.buildConstraintViolationWithTemplate("File cannot be empty!").addConstraintViolation();
            return true;
        }

        // Check file size
        if(file.getSize() > MAX_FILE_SIZE){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("File size should be less than 2MB!").addConstraintViolation();
            return false;
        }

        // Check file resolution
//        try {
//            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
//            if(bufferedImage.getHeight() > 1080 || bufferedImage.getWidth() > 1920){
//                constraintValidatorContext.disableDefaultConstraintViolation();
//                constraintValidatorContext.buildConstraintViolationWithTemplate("File height should be less than 1080 & file width should be less than 1920!").addConstraintViolation();
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return true;
    }

}
