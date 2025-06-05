package com.roadregistry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a person in the RoadRegistry system.
 * Handles adding, updating, and managing demerits.
 */
public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private boolean isSuspended;
    private List<Demerit> demerits;

     // Constructor to initialize a person object

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.isSuspended = false;
        this.demerits = new ArrayList<>();
    }

    
    /**
     * Adds the person to the 'persons.txt' file after validations.
     * @return true if added successfully, false otherwise.
     */
    public boolean addPerson() {
        if (!isValidID(personID)) {
            System.out.println("Invalid input: Person ID is invalid.");
            return false;
        }
        
        if (!isValidAddress(address)) {
             System.out.println("Invalid input: Address is invalid.");
             return false;
        }
        
        if (!isValidDate(birthdate)) {
            System.out.println("Invalid input: Birthdate format should be DD-MM-YYYY.");
            return false;
        }
        // Writes the validated person details into 'persons.txt'

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("persons.txt", true))) {
            bw.write(toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    
     /**
     * Updates personal information with specific validation rules.
     * @return true if updated successfully, false otherwise.
     */
    public boolean updatePersonalDetails(String newID, String newFirst, String newLast, String newAddress, String newBirth) {
        int age = getAge(this.birthdate);
        boolean birthdayChanged = !this.birthdate.equals(newBirth);
        boolean idStartsEven = Character.getNumericValue(this.personID.charAt(0)) % 2 == 0;

        if (age < 18 && !this.address.equals(newAddress)){
            System.out.println("Invalid update: Person is under 18 and cannot change address.");
            return false;
        } 
        
        if (birthdayChanged && (!newFirst.equals(firstName) || !newLast.equals(lastName) || !newAddress.equals(address))){
            System.out.println("Invalid update: When birthday is changed, no other field can be changed.");
            return false;
        }
        
        if (idStartsEven && !newID.equals(personID)){
            System.out.println("Invalid update: ID starts with an even number and cannot be changed.");
            return false;
        } 

        if (!isValidID(newID) || !isValidAddress(newAddress) || !isValidDate(newBirth)){
            System.out.println("Invalid update: New person ID is invalid.");
            return false;
        }
      // Apply updates to fields
        this.personID = newID;
        this.firstName = newFirst;
        this.lastName = newLast;
        this.address = newAddress;
        this.birthdate = newBirth;
      // Rewrite the file with updated person info

        return rewriteFile();
    }

    /**
     * Adds demerit points and updates suspension status based on age and total points.
     * @return "Success" if added and written to file, otherwise "Failed".
     */
    public String addDemeritPoints(int points, String date) {
        if (!isValidDate(date) || points < 1 || points > 6) return "Failed";
        demerits.add(new Demerit(date, points));

        int totalPoints = getPointsLastTwoYears();
        int age = getAge(this.birthdate);
     // Suspension rule: stricter for those under 21
        if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
            isSuspended = true;
        }
        
        return rewriteFile() ? "Success" : "Failed";
    }
  

    // Validates if the person ID format matches required pattern
    private boolean isValidID(String id) {
        if (id.length() != 10) return false;
        if (!id.matches("^[2-9]{2}.*[^A-Za-z0-9]{2,}.*[A-Z]{2}$")) return false;
        return true;
    }

     // Validates address format (5-parts, separated by '|')
    private boolean isValidAddress(String addr) {
        return addr.matches("^\\d+\\|[^|]+\\|[^|]+\\|Victoria\\|[^|]+$");
    }

    // Checks if date is in correct format DD-MM-YYYY
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Returns age based on birthdate string
    private int getAge(String dob) {
        LocalDate birth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return LocalDate.now().getYear() - birth.getYear();
    }

    // Sums demerit points received within last 2 years

    private int getPointsLastTwoYears() {
        LocalDate now = LocalDate.now();
        return demerits.stream()
            .filter(d -> LocalDate.parse(d.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).isAfter(now.minusYears(2)))
            .mapToInt(d -> d.points).sum();
    }

    // Rewrites 'persons.txt' to reflect latest state of the person

    private boolean rewriteFile() {
        File file = new File("persons.txt");
        if (!file.exists()) return false;

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(this.personID)) lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        lines.add(this.toString());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    // Converts person object into string format for file storage
    @Override
    public String toString() {
        return personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthdate + "|" + isSuspended;
    }

    /**
     * Inner class representing a single demerit entry.
     */

    private static class Demerit {
        String date;
        int points;

        Demerit(String date, int points) {
            this.date = date;
            this.points = points;
        }
    }
}
