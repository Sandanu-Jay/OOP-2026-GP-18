package com.faculty.management.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Central Modern Design System and Theme for Faculty Management System.
 * Clean, modern, high-contrast flat styling with professional palette.
 */
public class UITheme {

    // Background & Surface
    public static final Color BG_MAIN = new Color(248, 250, 252);       // #F8FAFC
    public static final Color CARD_BG = new Color(255, 255, 255);       // #FFFFFF
    public static final Color SIDEBAR_BG = new Color(15, 23, 42);       // #0F172A (Deep Slate)
    public static final Color SIDEBAR_HOVER = new Color(30, 41, 59);    // #1E293B
    public static final Color SIDEBAR_ACTIVE = new Color(37, 99, 235);  // #2563EB (Royal Blue)

    // Brand & Status Colors
    public static final Color PRIMARY = new Color(37, 99, 235);         // #2563EB
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);   // #1D4ED8
    public static final Color SUCCESS = new Color(16, 185, 129);        // #10B981
    public static final Color SUCCESS_BG = new Color(209, 250, 229);     // #D1FAE5
    public static final Color DANGER = new Color(239, 68, 68);          // #EF4444
    public static final Color DANGER_BG = new Color(254, 226, 226);      // #FEE2E2
    public static final Color WARNING = new Color(245, 158, 11);        // #F59E0B
    public static final Color WARNING_BG = new Color(254, 243, 199);    // #FEF3C7
    public static final Color INFO = new Color(59, 130, 246);           // #3B82F6
    public static final Color INFO_BG = new Color(219, 234, 254);       // #DBEAFE

    // Text & Borders
    public static final Color TEXT_DARK = new Color(15, 23, 42);        // #0F172A
    public static final Color TEXT_MUTED = new Color(100, 116, 139);    // #64748B
    public static final Color TEXT_LIGHT = new Color(248, 250, 252);    // #F8FAFC
    public static final Color BORDER = new Color(226, 232, 240);        // #E2E8F0
    public static final Color TABLE_HEADER_BG = new Color(241, 245, 249);// #F1F5F9
    public static final Color TABLE_ROW_ALT = new Color(248, 250, 252);  // #F8FAFC

    // Fonts (Standard fonts available on all systems)
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_KPI_VAL = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_KPI_TITLE = new Font("Segoe UI", Font.BOLD, 12);
}
