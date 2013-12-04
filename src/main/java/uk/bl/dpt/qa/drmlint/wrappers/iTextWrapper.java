/*
 * Copyright 2013 The British Library/SCAPE Project Consortium
 * Author: William Palmer (William.Palmer@bl.uk)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package uk.bl.dpt.qa.drmlint.wrappers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.exceptions.InvalidPdfException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

/**
 * Class to wrap iText for the purposes of extracting text from PDFs 
 * @author wpalmer
 *
 */
public class iTextWrapper {

	/**
	 * Extracts text from a PDF.
	 * @param pFile input file
	 * @param pOutput output file
	 * @param pOverwrite whether or not to overwrite an existing output file
	 * @return true if converted ok, otherwise false
	 */
	public static boolean extractTextFromPDF(File pFile, File pOutput, boolean pOverwrite) {
		if(pOutput.exists()&(!pOverwrite)) return false;
		
		boolean ret = true;
		
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(pOutput));
			PdfReader reader = new PdfReader(pFile.getAbsolutePath());
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			TextExtractionStrategy strategy;
			for(int i=0;i<reader.getNumberOfPages();i++) {
				try {
					//page numbers start at 1
					strategy = parser.processContent((i+1), new SimpleTextExtractionStrategy());
					//write text out to file
					pw.println(strategy.getResultantText());
				} catch(ExceptionConverter e) {
					e.printStackTrace();
					ret = false;
					pw.println("iText Exception: Page "+(i+1)+": "+e.getClass().getName()+": "+e.getMessage());
				}
			}
			pw.close();
			reader.close();
		} catch (IOException e) {
			ret = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * Check if a PDF file is valid or not
	 * @param pFile file to check
	 * @return whether the file is valid or not
	 */
	public static boolean isValid(File pFile) {
		
		boolean ret = false;

		try {
			PdfReader reader = new PdfReader(pFile.getAbsolutePath());
			for(int i=0;i<reader.getNumberOfPages();i++) {
				try {
					//page numbers start at 1
					PdfTextExtractor.getTextFromPage(reader, (i+1));
				} catch(Exception e) {
					
					e.printStackTrace();
					ret = false;
				}
			}
			reader.close();
			ret = true;
		} catch (BadPasswordException e) {
			//actually an error???
			ret = false;//???
			
		} catch (InvalidPdfException e) {
			ret = false;
			
		} catch (IOException e) {
			ret = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
			
		}

		return ret;
	}
	
	/**
	 * Check if a PDF file has DRM or not
	 * @param pFile file to check
	 * @return whether the file is had DRM or not
	 */
	public static boolean hasDRM(File pFile) {
		
		boolean drm = false;

		try {
			PdfReader reader = new PdfReader(pFile.getAbsolutePath());
			drm = reader.isEncrypted();
			reader.close();
		} catch (BadPasswordException e) {
			//assume drm
			drm = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return drm;
	}

}