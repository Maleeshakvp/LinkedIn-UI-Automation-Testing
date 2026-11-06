package lk.ac.kdu.sqa.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestNGCheck {

    @Test
    public void testNGIsWorking() {
        Assert.assertTrue(
                true,
                "TestNG is working correctly!"
        );

        System.out.println("TestNG is working correctly!");
    }
}

