package io.jenkins.plugins;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.OutputStream;
import java.io.IOException;

/**
 * An OutputStream wrapper with a list of values to mask from the output stream
 */
public class MaskingOutputStream extends OutputStream {
    private static final Logger LOGGER = Logger.getLogger(MaskingOutputStream.class.getName());

    private final OutputStream delegate;    

    public MaskingOutputStream(OutputStream delegate) {

        LOGGER.log(Level.FINE, "MaskingOutputStream constructor called");
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        this.delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    /*
        * Mask the sensitive data in the log
        */
    @Override
    public void write(@NonNull byte[] b, int off, int len) throws IOException {
        String in = new String(b, off, len);

        String regexPattern = "(?<![A-Za-z0-9\\+=])[A-Za-z0-9\\+=]{40}(?![A-Za-z0-9\\+=])";
        String replacementString = "****************************************";

        // Jenkins adds special code in the output to handle console notes.
        // Console notes are markuplanguage that the Jenkins console UI interprets 
        // to add links, colors, etc.
        // Console notes are a sequence of chars that starts with PREAMBLE_STR
        // and ends with POSTAMBLE_STR.
        // The logic below is to apply masking logic to the buffer portions
        // before and after the console note (if any) and skip masking the console 
        // note itself.
        int preamble_idx = in.indexOf(hudson.console.ConsoleNote.PREAMBLE_STR);
        int postamble_length = hudson.console.ConsoleNote.POSTAMBLE_STR.length();

        if (preamble_idx == -1) {
            String outString = in.replaceAll(regexPattern, replacementString);
            this.delegate.write(outString.getBytes());
            return;
        }

        int postamble_idx = in.indexOf(hudson.console.ConsoleNote.POSTAMBLE_STR);

        int leftStringLen = preamble_idx;
        int consoleNoteLen = postamble_idx + postamble_length - preamble_idx;
        int rightStringLen = len - consoleNoteLen - leftStringLen;

        if (leftStringLen > 0) {
            String left = new String(b, off, leftStringLen);
            String outString = left.replaceAll(regexPattern, replacementString);
            this.delegate.write(outString.getBytes());
        }        

        this.delegate.write(b, off + preamble_idx, consoleNoteLen);

        if (rightStringLen > 0) {
            String right = new String(b, off + postamble_idx + postamble_length, rightStringLen);
            String outString = right.replaceAll(regexPattern, replacementString);
            this.delegate.write(outString.getBytes());
        }
    } 
}
