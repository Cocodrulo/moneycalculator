package software.ulpgc.moneycalculator.architecture.model;

public record Currency(String code, String country, String image_url) {
    @Override
    public String toString() {
        return code;
    }
}
