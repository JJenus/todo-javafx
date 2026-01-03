package com.todoapp.util;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

public class AppColors {
    // Theme enum
    public enum Theme {
        DARK, LIGHT
    }
    
    private static Theme currentTheme = Theme.DARK;
    
    // Color palette constants for the blue theme
    private static final String DEEP_TWILIGHT = "#03045e";
    private static final String FRENCH_BLUE = "#023e8a";
    private static final String BRIGHT_TEAL_BLUE = "#0077b6";
    private static final String BLUE_GREEN = "#0096c7";
    private static final String TURQUOISE_SURF = "#00b4d8";
    private static final String SKY_AQUA = "#48cae4";
    private static final String FROSTED_BLUE = "#90e0ef";
    private static final String FROSTED_BLUE_2 = "#ade8f4";
    private static final String LIGHT_CYAN = "#caf0f8";
    
    // Complementary red palette for danger/error states
    private static final String DANGER_RED = "#d32f2f";
    private static final String DANGER_RED_HOVER = "#b71c1c";
    private static final String DANGER_RED_LIGHT = "#ffcdd2";
    
    // Neutral colors for text and backgrounds
    private static final String NEAR_WHITE = "#f8fafc";
    private static final String OFF_WHITE = "#f1f5f9";
    private static final String LIGHT_GRAY = "#e2e8f0";
    private static final String MEDIUM_GRAY = "#94a3b8";
    private static final String DARK_GRAY = "#475569";
    private static final String NEAR_BLACK = "#1e293b";
    
    private static final Map<String, Color> darkColors = new HashMap<>();
    private static final Map<String, Color> lightColors = new HashMap<>();
    
    // Initialize color palettes
    static {
        initializeDarkTheme();
        initializeLightTheme();
    }
    
    private static void initializeDarkTheme() {
        // Original dark theme colors
        darkColors.put("APP_BACKGROUND", Color.web("#626d5e"));
        darkColors.put("SURFACE", Color.web("#919c86"));
        darkColors.put("ELEVATED_SURFACE", Color.web("#c0caad"));
        
        darkColors.put("PRIMARY_TEXT", Color.web("#434269"));
        darkColors.put("SECONDARY_TEXT", Color.web("#784b73"));
        darkColors.put("DISABLED_TEXT", Color.web("#9e6772"));
        
        darkColors.put("PRIMARY_ACTION", Color.web("#8477bf"));
        darkColors.put("PRIMARY_ACTION_HOVER", Color.web("#a18df9"));
        darkColors.put("PRIMARY_ACTION_TEXT", Color.web("#ffffff"));
        
        darkColors.put("SECONDARY_ACTION", Color.web("#c1b09d"));
        darkColors.put("SECONDARY_ACTION_HOVER", Color.web("#b6a38f"));
        darkColors.put("SECONDARY_ACTION_TEXT", Color.web("#434269"));
        
        darkColors.put("DANGER", Color.web("#c47368"));
        darkColors.put("DANGER_HOVER", Color.web("#9e6772"));
        darkColors.put("DANGER_TEXT", Color.web("#ffffff"));
        
        darkColors.put("DEFAULT_ITEM_BG", Color.web("#c0caad"));
        darkColors.put("HOVER_ITEM_BG", Color.web("#c1b09d"));
        darkColors.put("SELECTED_ITEM_BG", Color.web("#919c86"));
        darkColors.put("COMPLETED_ITEM_BG", Color.web("#919c86"));
        darkColors.put("COMPLETED_TEXT", Color.web("#9e6772"));
        darkColors.put("COMPLETED_STRIKETHROUGH", Color.web("#784b73"));
        
        darkColors.put("INPUT_BACKGROUND", Color.web("#c0caad"));
        darkColors.put("INPUT_BORDER", Color.web("#919c86"));
        darkColors.put("INPUT_FOCUS_BORDER", Color.web("#8477bf"));
        darkColors.put("PLACEHOLDER_TEXT", Color.web("#784b73"));
        darkColors.put("CARET", Color.web("#434269"));
        
        darkColors.put("DEFAULT_ICON", Color.web("#434269"));
        darkColors.put("MUTED_ICON", Color.web("#784b73"));
        darkColors.put("ACTION_ICON", Color.web("#8477bf"));
        darkColors.put("DANGER_ICON", Color.web("#c47368"));
        
        darkColors.put("UNCHECKED_BG", Color.web("#c1b09d"));
        darkColors.put("CHECKED_BG", Color.web("#8477bf"));
        darkColors.put("CHECKMARK", Color.web("#ffffff"));
        darkColors.put("HOVER_OUTLINE", Color.web("#a18df9"));
        
        darkColors.put("DIVIDER", Color.web("#919c86"));
        darkColors.put("CARD_BORDER", Color.web("#626d5e"));
        darkColors.put("ERROR_TEXT", Color.web("#c47368"));
        darkColors.put("WARNING_TEXT", Color.web("#9e6772"));
    }
    
    private static void initializeLightTheme() {
        // Light theme using the blue palette with red for danger
        lightColors.put("APP_BACKGROUND", Color.web(NEAR_WHITE));
        lightColors.put("SURFACE", Color.web(OFF_WHITE));
        lightColors.put("ELEVATED_SURFACE", Color.web("#ffffff"));
        
        // Text colors
        lightColors.put("PRIMARY_TEXT", Color.web(DEEP_TWILIGHT));
        lightColors.put("SECONDARY_TEXT", Color.web(BRIGHT_TEAL_BLUE));
        lightColors.put("DISABLED_TEXT", Color.web(MEDIUM_GRAY));
        
        // Primary actions (using blues from the palette)
        lightColors.put("PRIMARY_ACTION", Color.web(BRIGHT_TEAL_BLUE));
        lightColors.put("PRIMARY_ACTION_HOVER", Color.web(BLUE_GREEN));
        lightColors.put("PRIMARY_ACTION_TEXT", Color.web("#ffffff"));
        
        // Secondary actions (light blue variants)
        lightColors.put("SECONDARY_ACTION", Color.web(FROSTED_BLUE_2));
        lightColors.put("SECONDARY_ACTION_HOVER", Color.web(SKY_AQUA));
        lightColors.put("SECONDARY_ACTION_TEXT", Color.web(DEEP_TWILIGHT));
        
        // Danger/error states (using complementary red)
        lightColors.put("DANGER", Color.web(DANGER_RED));
        lightColors.put("DANGER_HOVER", Color.web(DANGER_RED_HOVER));
        lightColors.put("DANGER_TEXT", Color.web("#ffffff"));
        
        // Item backgrounds
        lightColors.put("DEFAULT_ITEM_BG", Color.web("#ffffff"));
        lightColors.put("HOVER_ITEM_BG", Color.web(FROSTED_BLUE_2));
        lightColors.put("SELECTED_ITEM_BG", Color.web(SKY_AQUA));
        lightColors.put("COMPLETED_ITEM_BG", Color.web(LIGHT_CYAN));
        lightColors.put("COMPLETED_TEXT", Color.web(MEDIUM_GRAY));
        lightColors.put("COMPLETED_STRIKETHROUGH", Color.web(MEDIUM_GRAY));
        
        // Input styles
        lightColors.put("INPUT_BACKGROUND", Color.web("#ffffff"));
        lightColors.put("INPUT_BORDER", Color.web(LIGHT_GRAY));
        lightColors.put("INPUT_FOCUS_BORDER", Color.web(BRIGHT_TEAL_BLUE));
        lightColors.put("PLACEHOLDER_TEXT", Color.web(MEDIUM_GRAY));
        lightColors.put("CARET", Color.web(DEEP_TWILIGHT));
        
        // Icons
        lightColors.put("DEFAULT_ICON", Color.web(DARK_GRAY));
        lightColors.put("MUTED_ICON", Color.web(MEDIUM_GRAY));
        lightColors.put("ACTION_ICON", Color.web(BRIGHT_TEAL_BLUE));
        lightColors.put("DANGER_ICON", Color.web(DANGER_RED));
        
        // Checkboxes and selection
        lightColors.put("UNCHECKED_BG", Color.web(LIGHT_GRAY));
        lightColors.put("CHECKED_BG", Color.web(BRIGHT_TEAL_BLUE));
        lightColors.put("CHECKMARK", Color.web("#ffffff"));
        lightColors.put("HOVER_OUTLINE", Color.web(BLUE_GREEN));
        
        // Borders and dividers
        lightColors.put("DIVIDER", Color.web(LIGHT_GRAY));
        lightColors.put("CARD_BORDER", Color.web(LIGHT_GRAY));
        lightColors.put("ERROR_TEXT", Color.web(DANGER_RED));
        lightColors.put("WARNING_TEXT", Color.web("#f57c00"));
    }
    
    // Public getters for colors
    public static Color getAppBackground() {
        return getColor("APP_BACKGROUND");
    }
    
    public static Color getSurface() {
        return getColor("SURFACE");
    }
    
    public static Color getElevatedSurface() {
        return getColor("ELEVATED_SURFACE");
    }
    
    public static Color getPrimaryText() {
        return getColor("PRIMARY_TEXT");
    }
    
    public static Color getSecondaryText() {
        return getColor("SECONDARY_TEXT");
    }
    
    public static Color getDisabledText() {
        return getColor("DISABLED_TEXT");
    }
    
    public static Color getPrimaryAction() {
        return getColor("PRIMARY_ACTION");
    }
    
    public static Color getPrimaryActionHover() {
        return getColor("PRIMARY_ACTION_HOVER");
    }
    
    public static Color getPrimaryActionText() {
        return getColor("PRIMARY_ACTION_TEXT");
    }
    
    public static Color getSecondaryAction() {
        return getColor("SECONDARY_ACTION");
    }
    
    public static Color getSecondaryActionHover() {
        return getColor("SECONDARY_ACTION_HOVER");
    }
    
    public static Color getSecondaryActionText() {
        return getColor("SECONDARY_ACTION_TEXT");
    }
    
    public static Color getDanger() {
        return getColor("DANGER");
    }
    
    public static Color getDangerHover() {
        return getColor("DANGER_HOVER");
    }
    
    public static Color getDangerText() {
        return getColor("DANGER_TEXT");
    }
    
    public static Color getDefaultItemBg() {
        return getColor("DEFAULT_ITEM_BG");
    }
    
    public static Color getHoverItemBg() {
        return getColor("HOVER_ITEM_BG");
    }
    
    public static Color getSelectedItemBg() {
        return getColor("SELECTED_ITEM_BG");
    }
    
    public static Color getCompletedItemBg() {
        return getColor("COMPLETED_ITEM_BG");
    }
    
    public static Color getCompletedText() {
        return getColor("COMPLETED_TEXT");
    }
    
    public static Color getCompletedStrikethrough() {
        return getColor("COMPLETED_STRIKETHROUGH");
    }
    
    public static Color getInputBackground() {
        return getColor("INPUT_BACKGROUND");
    }
    
    public static Color getInputBorder() {
        return getColor("INPUT_BORDER");
    }
    
    public static Color getInputFocusBorder() {
        return getColor("INPUT_FOCUS_BORDER");
    }
    
    public static Color getPlaceholderText() {
        return getColor("PLACEHOLDER_TEXT");
    }
    
    public static Color getCaret() {
        return getColor("CARET");
    }
    
    public static Color getDefaultIcon() {
        return getColor("DEFAULT_ICON");
    }
    
    public static Color getMutedIcon() {
        return getColor("MUTED_ICON");
    }
    
    public static Color getActionIcon() {
        return getColor("ACTION_ICON");
    }
    
    public static Color getDangerIcon() {
        return getColor("DANGER_ICON");
    }
    
    public static Color getUncheckedBg() {
        return getColor("UNCHECKED_BG");
    }
    
    public static Color getCheckedBg() {
        return getColor("CHECKED_BG");
    }
    
    public static Color getCheckmark() {
        return getColor("CHECKMARK");
    }
    
    public static Color getHoverOutline() {
        return getColor("HOVER_OUTLINE");
    }
    
    public static Color getDivider() {
        return getColor("DIVIDER");
    }
    
    public static Color getCardBorder() {
        return getColor("CARD_BORDER");
    }
    
    public static Color getErrorText() {
        return getColor("ERROR_TEXT");
    }
    
    public static Color getWarningText() {
        return getColor("WARNING_TEXT");
    }
    
    // Theme management methods
    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public static void toggleTheme() {
        currentTheme = (currentTheme == Theme.DARK) ? Theme.LIGHT : Theme.DARK;
    }
    
    // Helper method to get color based on current theme
    private static Color getColor(String colorName) {
        if (currentTheme == Theme.LIGHT) {
            return lightColors.getOrDefault(colorName, Color.WHITE);
        } else {
            return darkColors.getOrDefault(colorName, Color.BLACK);
        }
    }
    
    // Utility method to get CSS color string
    public static String toCss(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    // Utility method to get CSS with opacity
    public static String toCss(Color color, double opacity) {
        return String.format("rgba(%d, %d, %d, %.2f)",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255),
            opacity);
    }
    
    // Get specific colors from the blue palette (useful for custom styling)
    public static Color getDeepTwilight() {
        return Color.web(DEEP_TWILIGHT);
    }
    
    public static Color getFrenchBlue() {
        return Color.web(FRENCH_BLUE);
    }
    
    public static Color getBrightTealBlue() {
        return Color.web(BRIGHT_TEAL_BLUE);
    }
    
    public static Color getBlueGreen() {
        return Color.web(BLUE_GREEN);
    }
    
    public static Color getTurquoiseSurf() {
        return Color.web(TURQUOISE_SURF);
    }
    
    public static Color getSkyAqua() {
        return Color.web(SKY_AQUA);
    }
    
    public static Color getFrostedBlue() {
        return Color.web(FROSTED_BLUE);
    }
    
    public static Color getFrostedBlue2() {
        return Color.web(FROSTED_BLUE_2);
    }
    
    public static Color getLightCyan() {
        return Color.web(LIGHT_CYAN);
    }
    
    // Get danger red colors
    public static Color getDangerRed() {
        return Color.web(DANGER_RED);
    }
    
    public static Color getDangerRedHover() {
        return Color.web(DANGER_RED_HOVER);
    }
    
    public static Color getDangerRedLight() {
        return Color.web(DANGER_RED_LIGHT);
    }
}