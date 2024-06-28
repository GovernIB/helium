/**
 * 
 */
package net.conselldemallorca.helium.webapp.mvc.util;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.displaytag.Messages;
import org.displaytag.exception.BaseNestableJspTagException;
import org.displaytag.exception.SeverityEnum;
import org.displaytag.export.BinaryExportView;
import org.displaytag.export.excel.ExcelHssfView;
import org.displaytag.model.Column;
import org.displaytag.model.ColumnIterator;
import org.displaytag.model.HeaderCell;
import org.displaytag.model.Row;
import org.displaytag.model.RowIterator;
import org.displaytag.model.TableModel;

/**
 * Exportació de dades per Displaytag emprant XSSF
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumHssfExportView implements BinaryExportView {

	/**
     * TableModel to render.
     */
    private TableModel model;

    /**
     * export full list?
     */
    private boolean exportFull;

    /**
     * include header in export?
     */
    private boolean header;

    /**
     * decorate export?
     */
    private boolean decorated;

    /**
     * @see org.displaytag.export.ExportView#setParameters(TableModel, boolean, boolean, boolean)
     */
    public void setParameters(TableModel tableModel, boolean exportFullList, boolean includeHeader,
        boolean decorateValues)
    {
        this.model = tableModel;
        this.exportFull = exportFullList;
        this.header = includeHeader;
        this.decorated = decorateValues;
    }

    /**
     * @return "application/vnd.ms-excel"
     * @see org.displaytag.export.BaseExportView#getMimeType()
     */
    public String getMimeType()
    {
        return "application/vnd.ms-excel"; //$NON-NLS-1$
    }

    /**
     * @see org.displaytag.export.BinaryExportView#doExport(OutputStream)
     */
    @SuppressWarnings("rawtypes")
	public void doExport(OutputStream out) throws JspException
    {
        try
        {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("-");

            int rowNum = 0;
            int colNum = 0;

            if (this.header)
            {
                // Create an header row
                XSSFRow xlsRow = sheet.createRow(rowNum++);

                XSSFCellStyle headerStyle = wb.createCellStyle();
                headerStyle.setFillPattern(XSSFCellStyle.FINE_DOTS);
                headerStyle.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
                XSSFFont bold = wb.createFont();
                bold.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                bold.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(bold);

                Iterator iterator = this.model.getHeaderCellList().iterator();

                while (iterator.hasNext())
                {
                    HeaderCell headerCell = (HeaderCell) iterator.next();

                    String columnHeader = headerCell.getTitle();

                    if (columnHeader == null)
                    {
                        columnHeader = StringUtils.capitalize(headerCell.getBeanPropertyName());
                    }

                    XSSFCell cell = xlsRow.createCell(colNum++);
                    cell.setCellValue(new XSSFRichTextString(columnHeader));
                    cell.setCellStyle(headerStyle);
                }
            }

            // get the correct iterator (full or partial list according to the exportFull field)
            RowIterator rowIterator = this.model.getRowIterator(this.exportFull);
            // iterator on rows

            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                XSSFRow xlsRow = sheet.createRow(rowNum++);
                colNum = 0;

                // iterator on columns
                ColumnIterator columnIterator = row.getColumnIterator(this.model.getHeaderCellList());

                while (columnIterator.hasNext())
                {
                    Column column = columnIterator.nextColumn();

                    // Get the value to be displayed for the column
                    Object value = column.getValue(this.decorated);
                    XSSFCell cell = xlsRow.createCell(colNum++);
                    writeCell(value, cell, wb);
                }
            }

            // adjust the column widths
            int colCount = 0;
            while (colCount <= colNum)
            {
                sheet.autoSizeColumn((short) colCount++);
            }

            wb.write(out);
        }
        catch (Exception e)
        {
        	logger.error("Error al exportar en format Excel", e);
            throw new ExcelGenerationException(e);
        }
    }

    /**
     * Write the value to the cell. Override this method if you have complex data types that may need to be exported.
     * @param value the value of the cell
     * @param cell the cell to write it to
     */
	protected void writeCell(Object value, XSSFCell cell, XSSFWorkbook wb)
    {
        if (value instanceof Number)
        {
            Number num = (Number) value;
            cell.setCellValue(num.doubleValue());
        }
        else if (value instanceof Date)
        {
        	XSSFCellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(
		    		wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		    cell.setCellStyle(cellStyle);
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
            cell.setCellValue((Date) value);
        }
        else if (value instanceof Calendar)
        {
            cell.setCellValue((Calendar) value);
        }
        else
        {
            cell.setCellValue(new XSSFRichTextString(escapeColumnValue(value)));
        }
    }

    // patch from Karsten Voges
    /**
     * Escape certain values that are not permitted in excel cells.
     * @param rawValue the object value
     * @return the escaped value
     */
    protected String escapeColumnValue(Object rawValue)
    {
        if (rawValue == null)
        {
            return null;
        }
        String returnString = ObjectUtils.toString(rawValue);
        boolean containsHtml = returnString.contains("<") && returnString.contains("/>");
		if (!containsHtml) {
	        // escape the String to get the tabs, returns, newline explicit as \t \r \n
	        returnString = StringEscapeUtils.escapeJava(StringUtils.trimToEmpty(returnString));
	        // remove tabs, insert four whitespaces instead
	        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\t", "    ");
	        // remove the return, only newline valid in excel
	        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\r", " ");
	        //si el camp és múltiple mostra una llista amb tots els valors
	        if(returnString.contains("td")){
	        	returnString = returnString.replaceAll("\\<.*?\\>", "");
	        	returnString = StringUtils.replace(StringUtils.trim(returnString), "\\n", "");
	        	returnString = StringUtils.stripToEmpty(returnString);
	        	String[] dades =StringUtils.splitByWholeSeparator(returnString, null);
	        	String sortida ="[";
	        	for(int i=0;i<dades.length;i++){
	        		if(i<dades.length && i>0){
	        			sortida+=", ";
	        		}
	        		sortida+=dades[i];
	        	}
	        	sortida=sortida.trim();
	        	sortida+="]";
	        	returnString = sortida;
	        }
	        // unescape so that \n gets back to newline
	        returnString = StringEscapeUtils.unescapeJava(returnString);
	        
	        
		} else {
			returnString = StringEscapeUtils.escapeJava(StringUtils.trimToEmpty(returnString));
			returnString = StringUtils.replace(StringUtils.trim(returnString), "\\t", "");
	        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\r", "");
	        returnString = StringUtils.replace(StringUtils.trim(returnString), "\\n", "");
	        returnString = StringUtils.replace(returnString, "<br\\/>", "\n");
	        returnString = StringUtils.replace(returnString, "<BR\\/>", "\n");
	        returnString = StringUtils.replace(returnString, "<br \\/>", "\n");
	        returnString = StringUtils.replace(returnString, "<BR \\/>", "\n");
	        returnString = StringUtils.replace(returnString, "<\\/tr>", "\n");
	        returnString = StringUtils.replace(returnString, "<\\/TR>", "\n");
	        returnString = returnString.replaceAll("<(.|\n)*?>", "");
		}
        return returnString;
    }	


    /**
     * Wraps IText-generated exceptions.
     * @author Fabrizio Giustina
     * @version $Revision: 1143 $ ($Author: fgiust $)
     */
    static class ExcelGenerationException extends BaseNestableJspTagException
    {

        /**
         * D1597A17A6.
         */
        private static final long serialVersionUID = 899149338534L;

        /**
         * Instantiate a new PdfGenerationException with a fixed message and the given cause.
         * @param cause Previous exception
         */
        public ExcelGenerationException(Throwable cause)
        {
            super(ExcelHssfView.class, Messages.getString("ExcelView.errorexporting"), cause); //$NON-NLS-1$
        }

        /**
         * @see org.displaytag.exception.BaseNestableJspTagException#getSeverity()
         */
        public SeverityEnum getSeverity()
        {
            return SeverityEnum.ERROR;
        }
    }
	
	/*@Override
	protected void writeCell(Object value, XSSFCell cell) {
		if (value instanceof Number) {
			Number num = (Number)value;
			cell.setCellValue(num.doubleValue());
		} else if (value instanceof Date) {
			CellStyle cellStyle = wb.createCellStyle();
		    cellStyle.setDataFormat(
		        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
		    cell = row.createCell(1);
		    cell.setCellValue(new Date());
		    cell.setCellStyle(cellStyle);
			cell.setCellType(cell.CELL_TYPE_NUMERIC);
			cell.setCellValue((Date)value);
		} else if (value instanceof Calendar) {
			cell.setCellValue((Calendar)value);
		} else {
			cell.setCellValue(new XSSFRichTextString(escapeColumnValue(value)));
		}
	}*/

	private static final Log logger = LogFactory.getLog(HeliumHssfExportView.class);

}
