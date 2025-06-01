package com.roadregistry;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;


/**
 * Represents a person in the RoadRegistry system.
 * Manages addition, update, and demerit logic for licensing.
 */
public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private boolean isSuspended;
    private List<Demerit> demerits;

    
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
     * Adds the person to a file if all conditions are satisfied.
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

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("persons.txt", true))) {
            bw.write(toString());
            bw.newLine();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    
    /**
     * Updates a person's personal details in the file if all conditions are satisfied.
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

        this.personID = newID;
        this.firstName = newFirst;
        this.lastName = newLast;
        this.address = newAddress;
        this.birthdate = newBirth;

        return rewriteFile();
    }

    /**
     * Adds demerit points to the person and updates suspension status.
     * @return "Success" if added, "Failed" if not.
     */
    public String addDemeritPoints(int points, String date) {
        if (!isValidDate(date) || points < 1 || points > 6) return "Failed";
        demerits.add(new Demerit(date, points));

        int totalPoints = getPointsLastTwoYears();
        int age = getAge(this.birthdate);

        if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
            isSuspended = true;
        }
        
        return rewriteFile() ? "Success" : "Failed";
    }
  

    /**
     * Validates the person ID format.
     */
    private boolean isValidID(String id) {
        if (id.length() != 10) return false;
        if (!id.matches("^[2-9]{2}.*[^A-Za-z0-9]{2,}.*[A-Z]{2}$")) return false;
        return true;
    }


    private boolean isValidAddress(String addr) {
        return addr.matches("^\\d+\\|[^|]+\\|[^|]+\\|Victoria\\|[^|]+$");
    }

    /**
     * Validates the date format (DD-MM-YYYY).
     */
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculates person's age from birthdate.
     */
    private int getAge(String dob) {
        LocalDate birth = LocalDate.parse(dob, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return LocalDate.now().getYear() - birth.getYear();
    }

    /**
     * Calculates total demerit points in the past 2 years.
     */
    private int getPointsLastTwoYears() {
        LocalDate now = LocalDate.now();
        return demerits.stream()
            .filter(d -> LocalDate.parse(d.date, DateTimeFormatter.ofPattern("dd-MM-yyyy")).isAfter(now.minusYears(2)))
            .mapToInt(d -> d.points).sum();
    }

    /**
     * Rewrites the file with updated person data.
     */
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

    /**
     * Converts the person object to a string line for TXT file.
     */
    @Override
    public String toString() {
        return personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthdate + "|" + isSuspended;
    }

    /**
     * Inner class to manage demerit entries.
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
