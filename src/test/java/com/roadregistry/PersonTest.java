package com.roadregistry;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
public class PersonTest {
    // Clears persons.txt file before all tests to avoid duplicates
    @BeforeAll
    static void fileClear(){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("persons.txt"))){
            writer.write("");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
        // -------------------- ADD PERSON TESTS --------------------

// Valid: Correct ID, date, address structure
    @Test
    void testAddPerson_Valid1(){
        Person p = new Person("56s_d%&fAB", "Peter", "Parker", "32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(p.addPerson());
        System.out.println("testAddPerson_Valid1: Test successful so true");
    }
    // Valid: Another successful add case
    @Test
    void testAddPerson_Valid2(){
        Person p = new Person("38s_d%&fDF", "Bruce", "Lee", "25|Highland Street|Geelong|Victoria|Australia", "15-10-1991");
        assertTrue(p.addPerson());
        System.out.println("testAddPerson_Valid2: Test successful so true");
    }
    // Valid: Adding person with a different location
    @Test
    void testAddPerson_Valid3(){
        Person p = new Person("64s_d%&fCZ", "Tom", "Ray", "45|Highland Street|Bendigo|Victoria|Australia", "15-09-1989");
        assertTrue(p.addPerson());
        System.out.println("testAddPerson_Valid3: Test successful so true");
    }
    // Invalid: Wrong ID and birthdate format, state mismatch
    @Test
    void testAddPerson_Invalid1(){
        Person p = new Person("64sehc78exfCZ", "John", "Walker", "25|Highland Street|Geelong|Queensland|Australia", "10-15-1991");
        assertFalse(p.addPerson());
        System.out.println("testAddPerson_Invalid1: Test successful so false");
    }
    // Invalid: Date format is wrong (YYYY-MM-DD)
    @Test
    void testAddPerson_Invalid2(){
        Person p = new Person("123s_d&fCZ", "Paul", "Collins", "25|Highland Street|Geelong|Victoria|Australia", "1995-12-10");
        assertFalse(p.addPerson());
        System.out.println("testAddPerson_Invalid2: Test successful so false");
    }

    // -------------------- UPDATE PERSONAL DETAILS TESTS --------------------
    // Valid: No fields actually changed
    @Test
    void testUpdateDetails_Valid1() {
        Person p = new Person("66s_d%&fAB", "Sam", "Smith", "12|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        p.addPerson(); 
        boolean updated = p.updatePersonalDetails("66s_d%&fAB", "Sam", "Smith", "12|Main Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(updated);
        System.out.println("testUpdateDetails_Valid1: Test successful so true");
    }
    // Valid: Person over 18, all fields same
    @Test
    void testUpdateDetails_Valid2() {
        Person p = new Person("65s_d%&fAB", "Ben", "Smith", "12|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        p.addPerson(); 
        boolean updated = p.updatePersonalDetails("65s_d%&fAB", "Ben", "Smith", "12|Main Street|Melbourne|Victoria|Australia", "15-11-2000");
        assertTrue(updated);
        System.out.println("testUpdateDetails_Valid2: Test successful so true");
    }
    // Valid: Changing names only
    @Test
    void testUpdateDetails_Valid3() {
        Person p = new Person("45s_d%&fAB", "Ben", "Smith", "12|Main Street|Melbourne|Victoria|Australia", "15-11-1995");
        p.addPerson(); 
        boolean updated = p.updatePersonalDetails("45s_d%&fAB", "Chris", "Parker", "12|Main Street|Melbourne|Victoria|Australia", "15-11-1995");
        assertTrue(updated);
        System.out.println("testUpdateDetails_Valid3: Test successful so true");
    }
    // Invalid: Under 18 and tries to change address.
    @Test
    void testUpdateDetails_Invalid1() {
        Person p = new Person("55s_d%&fAB", "Liam", "Evans", "25|Highland Street|Geelong|Victoria|Australia", "15-11-2018");
        assertFalse(p.updatePersonalDetails("55s_d%&fAB", "Lily", "Evans", "Some|Other|Address|Victoria|Australia", "15-11-2018"));
        System.out.println("testUpdateDetails_Invalid1: Test successful so false");
    }
    // Invalid: Birthdate change along with other changes
    @Test
    void testUpdateDetails_Invalid2() {
        Person p = new Person("79s_d%&fAB", "Matt", "Kennedy", "25|Highland Street|Geelong|Victoria|Australia", "15-11-2010");
        assertFalse(p.updatePersonalDetails("79s_d%&fAB", "Lily", "Evans", "Some|Other|Address|Victoria|Australia", "15-11-2009"));
        System.out.println("testUpdateDetails_Invalid2: Test successful so false");
    }
    // -------------------- ADD DEMERIT POINTS TESTS --------------------
    // Valid: Person gets 5 points, below threshold
    @Test
    void testAddDemeritPoints_Valid1() {
        Person p = new Person("45s_d%&fAB", "Adam", "Eve", "10|King St|Melbourne|Victoria|Australia", "01-01-2000");
        String result = p.addDemeritPoints(5, "23-08-2021");
        assertEquals("Success", result);
        System.out.println("testAddDemeritPoints_Valid1: Test successful so true");
    }
    // Valid: Person gets 3 points, older date
    @Test
    void testAddDemeritPoints_Valid2() {
        Person p = new Person("65s_d%&fAB", "Bobby", "Fischer", "10|King St|Melbourne|Victoria|Australia", "02-01-2000");
        String result = p.addDemeritPoints(3, "27-08-2019");
        assertEquals("Success", result);
        System.out.println("testAddDemeritPoints_Valid2: Test successful so true");
    }
    // Valid: Near recent date with 2 points
    @Test
    void testAddDemeritPoints_Valid3() {
        Person p = new Person("65s_d%&fAB", "Matt", "Hunt", "10|King St|Melbourne|Victoria|Australia", "03-01-2000");
        String result = p.addDemeritPoints(2, "31-12-2022");
        assertEquals("Success", result);
        System.out.println("testAddDemeritPoints_Valid3: Test successful so true");
    }
    // Invalid: Points exceed max limit (7)
    @Test
    void testAddDemeritPoints_Invalid1() {
        Person p = new Person("45s_d%&fAB", "James", "King", "10|King St|Melbourne|Victoria|Australia", "01-01-2000");
        String result = p.addDemeritPoints(7, "2025-11-11");
        assertEquals("Failed", result);
        System.out.println("testAddDemeritPoints_Invalid1: Test successful so false");
    }
    // Invalid: Bad date format and high points.
    @Test
    void testAddDemeritPoints_Invalid2() {
        Person p = new Person("45s_d%&fAB", "Jordan", "Kidd", "10|King St|Melbourne|Victoria|Australia", "01-01-2000");
        String result = p.addDemeritPoints(10, "2005-9-11");
        assertEquals("Failed", result);
        System.out.println("testAddDemeritPoints_Invalid2: Test successful so false");
    }
    

}
