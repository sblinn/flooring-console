
package com.sblinn.flooringmastery.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 *
 * @author sarablinn
 */
public class UserIOConsoleImpl implements UserIO {
    
    private Scanner console = new Scanner(System.in);

    @Override
    public void print(String msg) {
        System.out.println(msg);
    }

    @Override
    public String readString(String msgPrompt) {
        System.out.println(msgPrompt);
        return console.nextLine();
    }

    @Override
    public int readInt(String msgPrompt) {
        boolean invalidInput = true;
        int num = 0;
        while (invalidInput) {
            try {
                // print the message msgPrompt 
                String stringValue = this.readString(msgPrompt);
                // Get the input line, and try and parse
                // if non-integer character is entered, exception will be caught
                num = Integer.parseInt(stringValue); 
                invalidInput = false; // or you can use 'break;'
            } catch (NumberFormatException e) {
                this.print("Invalid input. Please try again.");
            }
        }
        return num;
    }

    @Override
    public int readInt(String msgPrompt, int min, int max) {
        int result = readInt(msgPrompt);
        while (result < min || result > max) {
            print("Invalid input. Please try again.");
            result = readInt(msgPrompt);
        }
        return result;
    }

    /**
     * Reads in a String and returns a BigDecimal value. When allowNull 
     * parameter is true, if user input is blank the method will return null.
     * 
     * @param msgPrompt
     * @param allowNull Boolean 
     * @return BigDecimal
     */
    @Override
    public BigDecimal readBigDecimal(String msgPrompt, boolean allowNull) {
        boolean invalidInput = true;
        BigDecimal dNum = new BigDecimal("0");
        while (invalidInput) {
            try {
                // print the message msgPrompt 
                String stringValue = this.readString(msgPrompt).trim();
                
                if (allowNull == true && stringValue.equals("")) {
                    return null;
                }
                
                dNum = new BigDecimal(stringValue);
                invalidInput = false; // or you can use 'break;'
            } catch (NumberFormatException e) {
                this.print("Invalid input. Please try again.");
            }
        }
        return dNum;
    }
    
    /**
     * Read a value input from the console and return the value as a BigDecimal 
     * if it is within the supplied range. If allowNull is true then method
     * will return null when user input is blank.
     * 
     * @param msgPrompt
     * @param min
     * @param max
     * @param allowNull Boolean
     * @return BigDecimal
     */
    @Override 
    public BigDecimal readBigDecimal(String msgPrompt, 
            BigDecimal min, BigDecimal max, boolean allowNull) {
        BigDecimal dNum = readBigDecimal(msgPrompt, allowNull);

        if (min == null && max == null) {
            dNum = readBigDecimal(msgPrompt, allowNull);
        }
        else if (min == null) {
            while (dNum.compareTo(max) > 0) {
                print("Invalid input. Please try again.");
                dNum = readBigDecimal(msgPrompt, allowNull);
            }
        }
        else if (max == null) {
            while (dNum.compareTo(min) < 0) {
                print("Invalid input. Please try again.");
                dNum = readBigDecimal(msgPrompt, allowNull);
            }
        }
        else {
            while (dNum.compareTo(min) < 0 || dNum.compareTo(max) > 0) {
                print("Invalid input. Please try again.");
                dNum = readBigDecimal(msgPrompt, allowNull);
            }
        }
        
        return dNum;
    }

    @Override
    public LocalDate readLocalDate(String msgPrompt) {
        boolean invalidInput = true;
        LocalDate date = LocalDate.now(); 
        while (invalidInput) {
            try {
                // print the message msgPrompt 
                String stringValue = this.readString(msgPrompt);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                date = LocalDate.parse(stringValue, formatter);
                invalidInput = false; 
                // if the input date is formatted incorrectly, exception is thrown.
            } catch (DateTimeParseException e) {
                this.print("Invalid input. Please check format and try again.");
            }
        }
        return date;
    }


    /**
     * Reads a user prompted String date later in time than minDate and equal or
     * sooner in time than maxDate and returns it as a LocalDate.
     * 
     * @param msgPrompt
     * @param minDate exclusive
     * @param maxDate inclusive
     * @return LocalDate
     */
    @Override
    public LocalDate readLocalDate(String msgPrompt,
            LocalDate minDate, LocalDate maxDate) {

        LocalDate date = readLocalDate(msgPrompt);
        
        if ((minDate == null && maxDate == null) 
                || (minDate == LocalDate.MIN && maxDate == LocalDate.MAX)) {
            return date;
        }
        if (minDate == null || minDate == LocalDate.MIN) {
            while (date.compareTo(maxDate) > 0) { // date is AFTER maxDate
                print("Error: Invalid input. Date must be before " 
                        + maxDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) 
                        + ". Please try again.");
                date = readLocalDate(msgPrompt);
            }
        }
        if (maxDate == null || maxDate == LocalDate.MAX) {
            while (date.compareTo(minDate) < 0) { // date is BEFORE minDate
                print("Error: Invalid input. Date must be after " 
                        + minDate.minusDays(1)
                            .format(DateTimeFormatter.ofPattern("MM-dd-yyyy")) 
                        + ". Please try again.");
                date = readLocalDate(msgPrompt);
            }
        }
        else {
            // if date is BEFORE minDate or date is AFTER maxDate
            while (date.compareTo(minDate) < 0 || date.compareTo(maxDate) > 0) {
                print("Invalid input. Please try again.");
                date = readLocalDate(msgPrompt);
            }
        }
       
        return date;
    }
    
    /**
     * Gets user input state abbreviation, checks that it is no more than two 
     * characters (blank is allowed) and returns the String.
     * 
     * @param msgPrompt
     * @return String state abbreviation
     */
    @Override
    public String readStateAbbreviation(String msgPrompt) {
        String inputStateAbbr = "";
        
        boolean invalidInput = true;
        while (invalidInput) {
            inputStateAbbr = readString(msgPrompt);
            if (inputStateAbbr.length() > 2) {
                print("Error: Invalid input. Please try again using a "
                        + "two-letter state abbreviation.");
            } else {
                invalidInput = false;
            }
        }
        
        return inputStateAbbr;
    }
    
    /**
     * Returns True if use inputs Yes or Y and returns False if user inputs
     * No or N.
     * 
     * @param msgPrompt
     * @return Boolean 
     */
    @Override
    public boolean readYesNoResponse(String msgPrompt) {
        boolean response = false; // default = no
        boolean invalidInput = true;
        while (invalidInput) {
            String input = this.readString(msgPrompt).trim();           
            if (input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("Y")) {
                response = true; // yes
                invalidInput = false;
            }
            else if (input.equalsIgnoreCase("NO") || input.equalsIgnoreCase("N")) {
                invalidInput = false;
            }
            else {
                print("Error: Invalid Input. Please respond with YES or NO.");
            }
        }
        
        return response;
    }
 
}
