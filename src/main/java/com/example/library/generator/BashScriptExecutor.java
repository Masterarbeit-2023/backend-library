package com.example.library.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BashScriptExecutor {
    public static void execute(String path, String argument) {
        try {
            // Change the script path if necessary

            // Set up the command with an argument
            ProcessBuilder pb = new ProcessBuilder("wsl", path, argument);

            // Start the process
            Process process = pb.start();

            // Read output
            StringBuilder out = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append('\n');
                System.out.println(line);
            }

            // Check result
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Execution successful: " + out.toString());
            } else {
                System.out.println("Execution failed");
            }

            // Get errors
            StringBuilder err = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = br.readLine()) != null) {
                System.err.println("Error line: " + line);
                err.append(line).append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
