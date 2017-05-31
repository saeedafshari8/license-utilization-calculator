package co.brtel.licenseutilizationcalculator.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import co.brtel.licenseutilizationcalculator.commons.Serializer;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;
import co.brtel.licenseutilizationcalculator.pojo.Raml;

public class RamlSerializer {
	private static final String RAML_HEAD_TEMPLATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><raml version=\"2.0\"><cmData type=\"actual\">";
	private static final String RAML_TAIL_TEMPLATE = "</cmData></raml>";
	private static final String MANAGED_OBJECT_CLOSING_TAG = "</managedObject>";

	public static Raml deserialize(String xml) throws JAXBException {
		Serializer<Raml> serializer = new Serializer<Raml>(new Raml());
		return serializer.deserialize(xml);
	}

	/**
	 * Extracts all ManagedObjects inside XML files. This function is useful for
	 * extracting objects in multiple files and when files are big.
	 * 
	 * @param filePathes
	 * @return
	 * @throws JAXBException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static List<ManagedObject> extractManagedObjects(List<String> filePathes) throws JAXBException, UnsupportedEncodingException, IOException {
		int progress = 0;
		int page = 20000;
		List<ManagedObject> managedObjects = new LinkedList<ManagedObject>();
		StringBuilder sb = new StringBuilder();

		for (String xmlStream : filePathes) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(xmlStream)), "UTF-8"))) {
				skipUnusedRamlParts(br);

				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append("\r\n");
					progress++;
					if (progress % page == 0) {
						if (!line.contains(MANAGED_OBJECT_CLOSING_TAG)) {
							line = br.readLine();
							while (!line.contains(MANAGED_OBJECT_CLOSING_TAG)) {
								sb.append(line);
								sb.append("\r\n");
								progress++;
								line = br.readLine();
							}
							sb.append(line);
							sb.append("\r\n");
							progress++;

							managedObjects.addAll(Arrays.asList(deserialize(RAML_HEAD_TEMPLATE + sb.toString() + RAML_TAIL_TEMPLATE).getCmData().getManagedObject()));
							sb = new StringBuilder();
							progress = 0;
						}
					}
				}
				if (progress > 0) {
					managedObjects.addAll(Arrays.asList(deserialize(RAML_HEAD_TEMPLATE + sb.toString().replace("</cmData>", "").replace("</raml>", "") + RAML_TAIL_TEMPLATE)
							.getCmData().getManagedObject()));
					sb = new StringBuilder();
					progress = 0;
				}
			}
		}

		return managedObjects;
	}

	/**
	 * Skips first four lines to make the file standard for deserialization
	 * 
	 * @param br
	 * @throws IOException
	 */
	private static void skipUnusedRamlParts(BufferedReader br) throws IOException {
		br.readLine();
		br.readLine();
		br.readLine();
		br.readLine();
	}
}
