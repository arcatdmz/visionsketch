package jp.junkato.vsketch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import jp.junkato.vsketch.interpreter.Input;

public class ConfigFile {
	private VsketchMain main;
    private String filePath;
    FileWriter fw;

    public ConfigFile(VsketchMain main, String filePath) {
    	this.main = main;
        this.filePath = filePath;
    }

    public void load() {
        BufferedReader br = null;
        try {
            FileReader fr = new FileReader(filePath);
            br = new BufferedReader(fr);
        } catch (FileNotFoundException e) {
            System.err.print("Config file not found: ");
            System.err.println(filePath);
            return;
        }
        String line;
        try {
            while ((line = br.readLine()) != null) {
                String[] words = line.split("=", 2);
                if (words.length < 2)
                    continue;
                deserialize(words[0].trim(), words[1].trim());
            }
            br.close();
        } catch (IOException e) {
            System.err.print("Error while reading the config file: ");
            System.err.println(filePath);
        }
    }

    public void save() {
        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            serialize(bw);
            bw.close();
        } catch (IOException e) {
            System.err.print("Error while writing config to the file: ");
            System.err.println(filePath);
        }
    }

    private void deserialize(String key, String value) {
        if ("source".equals(key)) {
        	main.getCode().setSource(value);
            return;
        }
    }

    private void serialize(BufferedWriter bw) throws IOException {
    	if (main.getCode() == null) {
    		return;
    	}
		Input input = main.getCode().getSource();
		if (input != null && input.getIdentifier() != null) {
	        bw.write("source = ");
	        bw.write(input.getIdentifier());
	        bw.newLine();
		}
    }
}
