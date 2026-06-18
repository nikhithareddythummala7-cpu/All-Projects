# TODO: Fix Gym Management System Maven Project Errors

## Tasks to Complete

- [ ] Update pom.xml: Remove version placeholders, hardcode versions, add Lombok annotation processor paths, add Jakarta Persistence dependency
- [ ] Update User.java: Change fullName to name, add missing fields (gender, age, profileImage, dateOfBirth), ensure Lombok annotations are correct
- [ ] Test compilation with `mvn clean install` to ensure no "cannot find symbol" errors

## Notes
- Lombok getters/setters not generating due to missing annotation processor configuration
- Jakarta Persistence needed for EntityNotFoundException
- User.java needs to match specified fields for Spring Security UserDetails implementation
