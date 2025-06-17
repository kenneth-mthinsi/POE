/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package poe;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author RC_Student_lab
 */
public class MessageNGTest {
    
    public MessageNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of sha256 method, of class Message.
     */
    @Test
    public void testSha256() {
        System.out.println("sha256");
        String txt = "";
        String expResult = "";
        String result = Message.sha256(txt);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of serialise method, of class Message.
     */
    @Test
    public void testSerialise() {
        System.out.println("serialise");
        Message instance = null;
        String expResult = "";
        String result = instance.serialise();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class Message.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        String line = "";
        Message expResult = null;
        Message result = Message.parse(line);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Message.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Message instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
