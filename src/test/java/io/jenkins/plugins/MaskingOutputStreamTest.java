package io.jenkins.plugins;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.*;

@RunWith(Parameterized.class)
public class MaskingOutputStreamTest {
    String input;    
    String expected;
    MaskingOutputStream os;

    @Before
    public void initialize() {        
         os = new MaskingOutputStream(System.out);
    }

    public MaskingOutputStreamTest(String input, String expected) {
        this.input = input;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection data() {
       return Arrays.asList(new Object[][] {
        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1This is an input string", "****************************************This is an input string"}, //**/
        {"This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "This is an input string****************************************"}, //**/
        {"This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1 ", "This is an input string**************************************** "}, //**/

        {"This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1\"", "This is an input string ****************************************\""},
        {"This is an input string \"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "This is an input string \"****************************************"},
        {"This is an input string\"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1\" ", "This is an input string\"****************************************\" "},
        {"This is an input string (+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1)", "This is an input string (****************************************)"},
        {"(+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1)", "(****************************************)"},
        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1 This is an input string", "**************************************** This is an input string"},          
        {"This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "This is an input string ****************************************"},
        

        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "****************************************"},
        {" +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1 ", " **************************************** "},
        {"(+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1)", "(****************************************)"},
        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1\n", "****************************************\n"},
        {"\n+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "\n****************************************"},
        {"\n+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1\n", "\n****************************************\n"},
        {"\n+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1 ", "\n**************************************** "},
        
        {"=\"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1", "=\"****************************************"},
        {"=\"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa1\"", "=\"****************************************\""},
        

        // secret is 1 char short, should match
        {"This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa", "This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa"},
        {"This is an input string (+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa)", "This is an input string (+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa)"},
        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa This is an input string", "+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa This is an input string"}, 
        {"+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1AaThis is an input string", "+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1AaThis is an input string"},
        {"This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa", "This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa"},
        {"This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa", "This is an input string +aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa"},
        {"This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa ", "This is an input string+aaa1aA1a1aa1Aaa8AAa1aA1aAAaA1+aAaAa1Aa "},
        // {"This is an input string", "This is an input string"},
       });
    }

    @Test
    public void testIndexOfNotFound() throws java.io.IOException{
        
        
        ByteArrayOutputStream byteArrayOutStr 
            = new ByteArrayOutputStream(); 
        MaskingOutputStream mos = new MaskingOutputStream(byteArrayOutStr);
        mos.write(input.getBytes(), 0, input.length());
            
        String out = byteArrayOutStr.toString();
        System.out.println(out);
        System.out.println(expected);
        Assert.assertEquals(expected, out);
    }
}
