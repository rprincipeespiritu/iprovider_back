package com.incloud.hcp.util;

import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.domain.TasaCambio;
import com.incloud.hcp.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
//import org.apache.poi.hwpf.HWPFDocument;
//import org.apache.poi.hwpf.usermodel.CharacterRun;
//import org.apache.poi.hwpf.usermodel.Paragraph;
//import org.apache.poi.hwpf.usermodel.Range;
//import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_ZONE_ID = "America/Lima";
    public static final String DATE_TIME_FORMAT_RESOURCES = "yyyy-MM-dd HH:mm";

    public static boolean isStringEmpty(String message) {
        return message == null || "".equals(message.trim());
    }


    public static ZonedDateTime getDefaultCurrentZonedDateTime() {
        return getCurrentZonedDateTime(ZoneId.of(DEFAULT_ZONE_ID));
    }

    public static ZonedDateTime getCurrentZonedDateTime(ZoneId zoneId) {
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
        return zonedDateTime;
    }

    public static ZonedDateTime getDefaultZonedDateTimeFromDate(Date date) {
        return getZonedDateTimeFromDate(date, ZoneId.of(DEFAULT_ZONE_ID));
    }

    public static ZonedDateTime getZonedDateTimeFromDate(Date date, ZoneId zoneId) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return zonedDateTime;
    }

    public static Optional<Date> getDateFromString(String date) {
        return getDateFromString(date, DEFAULT_DATE_FORMAT);
    }

    public static Optional<Date> getDateTimeFromString(String date) {
        return getDateFromString(date, DEFAULT_DATETIME_FORMAT);
    }

    public static Date getDevuelveFechaHora(String pfecha, String phora) {
         if (StringUtils.isBlank(pfecha)) {
            return null;
        }
        Date dFecha = getDateFromString(pfecha).orElse(null);
        if (StringUtils.isNotBlank(phora)) {
            String fechaHora = pfecha + " " + phora;
            dFecha = getDateFromString(fechaHora, DATE_TIME_FORMAT_RESOURCES).orElse(null);
        }
        return dFecha;
    }

    public static Optional<Date> getDateFromString(String date, String format) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            Date d = df.parse(date);
            return Optional.of(d);
        } catch (ParseException ex) {
            return Optional.empty();
        }
    }

    public static Optional<Date> zonedDateTimeToDate(ZonedDateTime zonedDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
        String date = dateTimeFormatter.format(zonedDateTime);
        return getDateFromString(date);
    }

    public static Optional<Date> zonedDateTimeToDateTime(ZonedDateTime zonedDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
        String date = dateTimeFormatter.format(zonedDateTime);
        return getDateTimeFromString(date);
    }
    
    public static String formatDate(Date d){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String s = df.format(d);
		return s;
	}

	public static String formatDateTime(Date dt){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String s = df.format(dt);
		return s;
	}

    public static Date getCurrentDate() {
        ZonedDateTime currentZonedDateTime = Utils.getDefaultCurrentZonedDateTime();
        Optional<Date> currentDateOptional = Utils.zonedDateTimeToDateTime(currentZonedDateTime);
        Date fechaProceso = currentDateOptional.orElseThrow(() -> new ServiceException("no se pudo obtener fecha del proceso"));
        return fechaProceso;
    }

    public static String obtieneMensajeErrorException(Exception e) {
        String retorno = "";
        if (Optional.ofNullable(e.getCause()).isPresent()) {
            if (Optional.ofNullable(e.getCause().getCause()).isPresent()) {
                if (Optional.ofNullable(e.getCause().getCause().getMessage()).isPresent()) {
                    retorno = e.getCause().getCause().getMessage();
                }

            }
            if (Optional.ofNullable(e.getCause().getMessage()).isPresent()) {
                if (Optional.ofNullable(retorno).isPresent()) {
                    retorno = retorno + " / " + e.getCause().getMessage();
                }
                return retorno;
            }
        }

        if (StringUtils.isNotBlank(e.getMessage()))
            return e.getMessage();
        if (StringUtils.isNotBlank(e.getLocalizedMessage()))
            return e.getLocalizedMessage();
        retorno = e.toString();
        return retorno;
    }

    public static Boolean validarEmail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);

        if (!email.isEmpty() && !mather.find()) { // si email no esta vacio debe cumplir el patron
            return false;
        }

        return true;
    }

    public static TasaCambio obtenerTasaCambioInvertida (TasaCambio tasaCambio){
        TasaCambio newTasaCambio = new TasaCambio();

        newTasaCambio.setValor(new BigDecimal("1.00").divide(tasaCambio.getValor(),4, BigDecimal.ROUND_HALF_UP));
        newTasaCambio.setIdMonedaOrigen(tasaCambio.getIdMonedaDestino());
        newTasaCambio.setIdMonedaDestino(tasaCambio.getIdMonedaOrigen());
        newTasaCambio.setFechaTasa(tasaCambio.getFechaTasa());

        return newTasaCambio;
    }

    public static String getValueNodo(Element elemento, String nombreNodo)
    {
        try
        {
            NodeList childNodes = elemento.getElementsByTagName(nombreNodo).item(0).getChildNodes();
            if(childNodes.getLength() > 0)
            {
                return  childNodes.item(0).getNodeValue();
            }
            return "";
        }
        catch (Exception e) {
            return "";
        }
    }

    public static Double getValueNodoNum(Element elemento, String nombreNodo)
    {
        try {
            NodeList childNodes = elemento.getElementsByTagName(nombreNodo).item(0).getChildNodes();
            if (childNodes.getLength() > 0) {
                return Double.valueOf(childNodes.item(0).getNodeValue());
            }
            return 0.0;
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    public static Date getValueNodoDate(Element elemento, String nombreNodo)
    {
        try {
            NodeList childNodes = elemento.getElementsByTagName(nombreNodo).item(0).getChildNodes();
            if (childNodes.getLength() > 0) {
                return Utils.getDateFromString(childNodes.item(0).getNodeValue(), "yyyy-MM-dd").get();
            }
            return Utils.getCurrentDate();
        }
        catch (Exception e) {
            return Utils.getCurrentDate();
        }
    }


    public static void remplazarTextoDocumento(XWPFDocument doc, String sKey, String sValue) throws Exception{

        for (XWPFParagraph p : doc.getParagraphs()) {
            List<XWPFRun> runs = p.getRuns();
            if (runs != null) {
                for (XWPFRun r : runs) {
                    String text = r.getText(0);
                    if (text != null && text.contains(sKey)) {
                        text = text.replace(sKey, sValue);
                        r.setText(text, 0);
                    }
                }
            }
        }

        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                text = text.replace(sKey, sValue);
                                r.setText(text,0);
                            }
                        }
                    }
                }
            }
        }

    }

    public static void remplazarTextoDocumentoTabla(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tabla1}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().getTblGrid().getGridColList();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    //cell.setText("RUTA");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    //cell2.setText("PERIODO");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");
                                    //cell3.setText("CAPACIDAD SOLICITADA (KPCD)");
                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell( row.getCell(3),"000000");
                                    //cell4.setText("CAPACIDAD OFRECIDA POR TGI (KPCD)");
                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell( row.getCell(4),"000000");
                                    //cell5.setText("OBSERVACIÓN");

                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Nro Antecedente" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Delitos" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Descripción Caso" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 7, "000000" , "Fecha Condena" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 7, "000000" , "Tipo" , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);


                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String numero = arrayLists.get(i).get(0).toString();
                                        String delitos = arrayLists.get(i).get(1).toString();
                                        String descripcion = arrayLists.get(i).get(2).toString();
                                        String fechacondena = arrayLists.get(i).get(3).toString();
                                        String tipo = arrayLists.get(i).get(4).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , numero , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , delitos , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , descripcion , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell( row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 7, "000000" , fechacondena , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell( row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 7, "000000" , tipo , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);

                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tabla1}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

    public static void remplazarTextoDocumentoTabla5Representante(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tabla5}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().getTblGrid().getGridColList();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    //cell.setText("RUTA");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    //cell2.setText("PERIODO");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");
                                    //cell3.setText("CAPACIDAD SOLICITADA (KPCD)");
                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell( row.getCell(3),"000000");
                                    //cell4.setText("CAPACIDAD OFRECIDA POR TGI (KPCD)");
                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell( row.getCell(4),"000000");
                                    //cell5.setText("OBSERVACIÓN");

                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Nro Antecedente" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Delitos" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Descripción Caso" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 7, "000000" , "Fecha Condena" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 7, "000000" , "Tipo" , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);


                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String numero = arrayLists.get(i).get(0).toString();
                                        String delitos = arrayLists.get(i).get(1).toString();
                                        String descripcion = arrayLists.get(i).get(2).toString();
                                        String fechacondena = arrayLists.get(i).get(3).toString();
                                        String tipo = arrayLists.get(i).get(4).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , numero , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , delitos , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , descripcion , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell( row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 7, "000000" , fechacondena , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell( row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 7, "000000" , tipo , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);

                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tabla5}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }



    public static void remplazarTextoDocumentoTablaPep(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tabla2}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");


                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Cargo ejercido" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Entidad" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Fecha de inicio y fin" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);



                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String cargo = arrayLists.get(i).get(0).toString();
                                        String entidad = arrayLists.get(i).get(1).toString();
                                        String fechaInicioFin = arrayLists.get(i).get(2).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , cargo , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , entidad , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , fechaInicioFin , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tabla2}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

    public static void remplazarTextoDocumentoTablaPepAccionista(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tablaPep1}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");


                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Cargo ejercido" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Entidad" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Fecha de inicio y fin" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);



                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String cargo = arrayLists.get(i).get(0).toString();
                                        String entidad = arrayLists.get(i).get(1).toString();
                                        String fechaInicioFin = arrayLists.get(i).get(2).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , cargo , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , entidad , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , fechaInicioFin , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tablaPep1}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

    private static void setRun (XWPFRun run , String fontFamily , int fontSize , String colorRGB , String text , boolean bold , boolean addBreak) {
        run.setFontFamily(fontFamily);
        run.setFontSize(fontSize);
        run.setColor(colorRGB);
        run.setText(text);
        run.setBold(bold);
        if (addBreak) run.addBreak();
    }


    public static void remplazarTextoDocumentoTablaParientePep(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tabla3}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell(cell1,"000000");

                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell(cell2,"000000");

                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell(cell3,"000000");

                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell(cell4,"000000");

                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell(cell5,"000000");

                                    XWPFTableCell cell6 = row.getCell(5); if (cell6 == null) cell6 = row.createCell();
                                    setBorderCell(cell6,"000000");

                                    XWPFTableCell cell7 = row.getCell(6); if (cell7 == null) cell7 = row.createCell();
                                    setBorderCell(cell7,"000000");




                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Vínculo parental" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Nombres" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Cargo ejercido" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 7, "000000" , "Entidad" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 7, "000000" , "Fecha de inicio y fin" , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph6 = row.getCell(5).addParagraph();
                                    setRun(paragraph6.createRun() , "Arial" , 7, "000000" , "Nacionalidad" , true, false);
                                    paragraph6.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph7 = row.getCell(6).addParagraph();
                                    setRun(paragraph7.createRun() , "Arial" , 7, "000000" , "DNI/Carné Ext/Pass" , true, false);
                                    paragraph7.setAlignment(ParagraphAlignment.CENTER);




                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");

                                        String vinculo = arrayLists.get(i).get(0).toString();
                                        String nombres = arrayLists.get(i).get(1).toString();
                                        String cargo = arrayLists.get(i).get(2).toString();
                                        String entidad = arrayLists.get(i).get(3).toString();
                                        String fechaInicioFin = arrayLists.get(i).get(4).toString();
                                        String nacionalidad = arrayLists.get(i).get(5).toString();
                                        String dni = arrayLists.get(i).get(6).toString();



                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell(row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , vinculo , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);
                                        t2.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"EAEAEA");

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell(row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , nombres , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell(row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , cargo , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell(row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 7, "000000" , entidad , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell(row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 7, "000000" , fechaInicioFin , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata6 = row2.getCell(5).addParagraph();
                                        setBorderCell(row2.getCell(5),"000000");
                                        setRun(paragraphdata6.createRun() , "Arial" , 7, "000000" , nacionalidad , false, false);
                                        paragraphdata6.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata7 = row2.getCell(6).addParagraph();
                                        setBorderCell(row2.getCell(6),"000000");
                                        setRun(paragraphdata7.createRun() , "Arial" , 7, "000000" , dni , false, false);
                                        paragraphdata7.setAlignment(ParagraphAlignment.CENTER);


                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tabla3}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

    public static void remplazarTextoDocumentoTablaParientePepAccionista(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tablaParientePep1}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(9000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell(cell1,"000000");

                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell(cell2,"000000");

                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell(cell3,"000000");

                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell(cell4,"000000");

                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell(cell5,"000000");

                                    XWPFTableCell cell6 = row.getCell(5); if (cell6 == null) cell6 = row.createCell();
                                    setBorderCell(cell6,"000000");

                                    XWPFTableCell cell7 = row.getCell(6); if (cell7 == null) cell7 = row.createCell();
                                    setBorderCell(cell7,"000000");




                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 7, "000000" , "Vínculo parental" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 7, "000000" , "Nombres" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 7, "000000" , "Cargo ejercido" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 7, "000000" , "Entidad" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 7, "000000" , "Fecha de inicio y fin" , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph6 = row.getCell(5).addParagraph();
                                    setRun(paragraph6.createRun() , "Arial" , 7, "000000" , "Nacionalidad" , true, false);
                                    paragraph6.setAlignment(ParagraphAlignment.CENTER);


                                    XWPFParagraph paragraph7 = row.getCell(6).addParagraph();
                                    setRun(paragraph7.createRun() , "Arial" , 7, "000000" , "DNI/Carné Ext/Pass" , true, false);
                                    paragraph7.setAlignment(ParagraphAlignment.CENTER);




                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");

                                        String vinculo = arrayLists.get(i).get(0).toString();
                                        String nombres = arrayLists.get(i).get(1).toString();
                                        String cargo = arrayLists.get(i).get(2).toString();
                                        String entidad = arrayLists.get(i).get(3).toString();
                                        String fechaInicioFin = arrayLists.get(i).get(4).toString();
                                        String nacionalidad = arrayLists.get(i).get(5).toString();
                                        String dni = arrayLists.get(i).get(6).toString();



                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell(row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 7, "000000" , vinculo , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);
                                        t2.setBottomBorder(XWPFTable.XWPFBorderType.SINGLE,1,1,"EAEAEA");

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell(row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 7, "000000" , nombres , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell(row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 7, "000000" , cargo , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell(row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 7, "000000" , entidad , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell(row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 7, "000000" , fechaInicioFin , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata6 = row2.getCell(5).addParagraph();
                                        setBorderCell(row2.getCell(5),"000000");
                                        setRun(paragraphdata6.createRun() , "Arial" , 7, "000000" , nacionalidad , false, false);
                                        paragraphdata6.setAlignment(ParagraphAlignment.CENTER);


                                        XWPFParagraph paragraphdata7 = row2.getCell(6).addParagraph();
                                        setBorderCell(row2.getCell(6),"000000");
                                        setRun(paragraphdata7.createRun() , "Arial" , 7, "000000" , dni , false, false);
                                        paragraphdata7.setAlignment(ParagraphAlignment.CENTER);


                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                }
                                else if(sKey.equals("{tablaParientePep1}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }


    public static void setBorderCell(XWPFTableCell cell, String Color ){
        CTTc ctTc = cell.getCTTc();
        ctTc.addNewTcPr();
        CTTcPr tcPr = ctTc.getTcPr();
        CTTcBorders border = tcPr.addNewTcBorders();

        border.addNewBottom().setVal(STBorder.DOTTED);
        border.addNewRight().setVal(STBorder.DOTTED);
        border.addNewLeft().setVal(STBorder.DOTTED);
        border.addNewTop().setVal(STBorder.DOTTED);
    }

    public static void remplazarTextoDocumentoTablaAccionistas(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tablaAccionistas1}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();
                                    XmlCursor cursor2 = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(10000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().getTblGrid().getGridColList();
                                    t2.setTableAlignment(TableRowAlign.CENTER);



                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    //cell.setText("RUTA");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    //cell2.setText("PERIODO");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");
                                    //cell3.setText("CAPACIDAD SOLICITADA (KPCD)");
                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell( row.getCell(3),"000000");
                                    //cell4.setText("CAPACIDAD OFRECIDA POR TGI (KPCD)");
                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell( row.getCell(4),"000000");
                                    //cell5.setText("OBSERVACIÓN");
                                    XWPFTableCell cell6 = row.getCell(5); if (cell6 == null) cell6 = row.createCell();
                                    setBorderCell( row.getCell(5),"000000");

                                    XWPFTableCell cell7 = row.getCell(6); if (cell7 == null) cell7 = row.createCell();
                                    setBorderCell( row.getCell(6),"000000");


                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 6, "000000" , "Nombres y apellidos / Razón Social (*):" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 6, "000000" , "Nacionalidad:" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 6, "000000" , "Otra (Indicar)" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 6, "000000" , "Documento de identidad" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 6, "000000" , "No." , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph6 = row.getCell(5).addParagraph();
                                    setRun(paragraph6.createRun() , "Arial" , 6, "000000" , "R.U.C. No. o equivalente." , true, false);
                                    paragraph6.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph7 = row.getCell(6).addParagraph();
                                    setRun(paragraph7.createRun() , "Arial" , 6, "000000" , "Porcentaje de participación" , true, false);
                                    paragraph7.setAlignment(ParagraphAlignment.CENTER);


                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String nombre = arrayLists.get(i).get(0).toString();
                                        String nacionalidad = arrayLists.get(i).get(1).toString();
                                        String otraNacionalidad = arrayLists.get(i).get(2).toString();
                                        String docIdentidad = arrayLists.get(i).get(3).toString();
                                        String numeroDoc = arrayLists.get(i).get(4).toString();
                                        String ruc = arrayLists.get(i).get(5).toString();
                                        String procentaje = arrayLists.get(i).get(6).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 6, "000000" , nombre , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 6, "000000" , nacionalidad , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 6, "000000" , otraNacionalidad , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell( row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 6, "000000" , docIdentidad , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell( row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 6, "000000" , numeroDoc , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata6 = row2.getCell(5).addParagraph();
                                        setBorderCell( row2.getCell(5),"000000");
                                        setRun(paragraphdata6.createRun() , "Arial" , 6, "000000" , ruc , false, false);
                                        paragraphdata6.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata7 = row2.getCell(6).addParagraph();
                                        setBorderCell( row2.getCell(6),"000000");
                                        setRun(paragraphdata7.createRun() , "Arial" , 6, "000000" , procentaje , false, false);
                                        paragraphdata7.setAlignment(ParagraphAlignment.CENTER);

                                        //CREAR SUBTABLA PARA PEP Y PARIENTE PEP ASOCIADOS



                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);


                                }
                                else if(sKey.equals("{tablaAccionistas1}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

    public static void remplazarTextoDocumentoTablaAccionistaPep(XWPFDocument doc, String sKey, ArrayList<ArrayList> arrayLists) throws Exception{

        System.out.println("");
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow rows : tbl.getRows()) {
                for (XWPFTableCell cell : rows.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun r : p.getRuns()) {
                            String text = r.getText(0);
                            if (text != null && text.contains(sKey)) {
                                System.out.println("");
                                if(sKey.equals("{tablaPep}"))
                                {
                                    /*Quitamos la clave y añadimos la tabla :D*/
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);

                                    XmlCursor cursor = p.getCTP().newCursor();
                                    XmlCursor cursor2 = p.getCTP().newCursor();

                                    XWPFTable t2 = p.getBody().insertNewTbl(cursor);
                                    t2.setWidth(10000);

                                    //tableSetBorders(t2,STBorder.SINGLE,2,1,"EAEAEA");
                                    XWPFTableRow row = t2.getRow(0); if (row == null) row = t2.createRow();

                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().addNewTblGrid().addNewGridCol();
                                    t2.getCTTbl().getTblGrid().getGridColList();
                                    t2.setTableAlignment(TableRowAlign.CENTER);

                                    XWPFTable t3 = p.getBody().insertNewTbl(cursor2);
                                    t3.setWidth(10000);


                                    XWPFTableCell cell1 = row.getCell(0); if (cell1 == null) cell1 = row.createCell();
                                    setBorderCell( row.getCell(0),"000000");
                                    //cell.setText("RUTA");
                                    XWPFTableCell cell2 = row.getCell(1); if (cell2 == null) cell2 = row.createCell();
                                    setBorderCell( row.getCell(1),"000000");
                                    //cell2.setText("PERIODO");
                                    XWPFTableCell cell3 = row.getCell(2); if (cell3 == null) cell3 = row.createCell();
                                    setBorderCell( row.getCell(2),"000000");
                                    //cell3.setText("CAPACIDAD SOLICITADA (KPCD)");
                                    XWPFTableCell cell4 = row.getCell(3); if (cell4 == null) cell4 = row.createCell();
                                    setBorderCell( row.getCell(3),"000000");
                                    //cell4.setText("CAPACIDAD OFRECIDA POR TGI (KPCD)");
                                    XWPFTableCell cell5 = row.getCell(4); if (cell5 == null) cell5 = row.createCell();
                                    setBorderCell( row.getCell(4),"000000");
                                    //cell5.setText("OBSERVACIÓN");
                                    XWPFTableCell cell6 = row.getCell(5); if (cell6 == null) cell6 = row.createCell();
                                    setBorderCell( row.getCell(5),"000000");

                                    XWPFTableCell cell7 = row.getCell(6); if (cell7 == null) cell7 = row.createCell();
                                    setBorderCell( row.getCell(6),"000000");


                                    XWPFParagraph paragraph = row.getCell(0).addParagraph();
                                    setRun(paragraph.createRun() , "Arial" , 6, "000000" , "Nombres y apellidos / Razón Social (*):" , true, false);
                                    paragraph.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph2 = row.getCell(1).addParagraph();
                                    setRun(paragraph2.createRun() , "Arial" , 6, "000000" , "Nacionalidad:" , true, false);
                                    paragraph2.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph3 = row.getCell(2).addParagraph();
                                    setRun(paragraph3.createRun() , "Arial" , 6, "000000" , "Otra (Indicar)" , true, false);
                                    paragraph3.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph4 = row.getCell(3).addParagraph();
                                    setRun(paragraph4.createRun() , "Arial" , 6, "000000" , "Documento de identidad" , true, false);
                                    paragraph4.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph5 = row.getCell(4).addParagraph();
                                    setRun(paragraph5.createRun() , "Arial" , 6, "000000" , "No." , true, false);
                                    paragraph5.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph6 = row.getCell(5).addParagraph();
                                    setRun(paragraph6.createRun() , "Arial" , 6, "000000" , "R.U.C. No. o equivalente." , true, false);
                                    paragraph6.setAlignment(ParagraphAlignment.CENTER);

                                    XWPFParagraph paragraph7 = row.getCell(6).addParagraph();
                                    setRun(paragraph7.createRun() , "Arial" , 6, "000000" , "Porcentaje de participación" , true, false);
                                    paragraph7.setAlignment(ParagraphAlignment.CENTER);


                                    for(int i = 0; i < arrayLists.size(); i++)
                                    {
                                        System.out.println("");
                                        String nombre = arrayLists.get(i).get(0).toString();
                                        String nacionalidad = arrayLists.get(i).get(1).toString();
                                        String otraNacionalidad = arrayLists.get(i).get(2).toString();
                                        String docIdentidad = arrayLists.get(i).get(3).toString();
                                        String numeroDoc = arrayLists.get(i).get(4).toString();
                                        String ruc = arrayLists.get(i).get(5).toString();
                                        String procentaje = arrayLists.get(i).get(6).toString();


                                        XWPFTableRow row2 = t2.getRow(i+1); if (row2 == null) row2 = t2.createRow();

                                        XWPFParagraph paragraphdata = row2.getCell(0).addParagraph();
                                        setBorderCell( row2.getCell(0),"000000");
                                        setRun(paragraphdata.createRun() , "Arial" , 6, "000000" , nombre , false, false);
                                        paragraphdata.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata2 = row2.getCell(1).addParagraph();
                                        setBorderCell( row2.getCell(1),"000000");
                                        setRun(paragraphdata2.createRun() , "Arial" , 6, "000000" , nacionalidad , false, false);
                                        paragraphdata2.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata3 = row2.getCell(2).addParagraph();
                                        setBorderCell( row2.getCell(2),"000000");
                                        setRun(paragraphdata3.createRun() , "Arial" , 6, "000000" , otraNacionalidad , false, false);
                                        paragraphdata3.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata4 = row2.getCell(3).addParagraph();
                                        setBorderCell( row2.getCell(3),"000000");
                                        setRun(paragraphdata4.createRun() , "Arial" , 6, "000000" , docIdentidad , false, false);
                                        paragraphdata4.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata5 = row2.getCell(4).addParagraph();
                                        setBorderCell( row2.getCell(4),"000000");
                                        setRun(paragraphdata5.createRun() , "Arial" , 6, "000000" , numeroDoc , false, false);
                                        paragraphdata5.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata6 = row2.getCell(5).addParagraph();
                                        setBorderCell( row2.getCell(5),"000000");
                                        setRun(paragraphdata6.createRun() , "Arial" , 6, "000000" , ruc , false, false);
                                        paragraphdata6.setAlignment(ParagraphAlignment.CENTER);

                                        XWPFParagraph paragraphdata7 = row2.getCell(6).addParagraph();
                                        setBorderCell( row2.getCell(6),"000000");
                                        setRun(paragraphdata7.createRun() , "Arial" , 6, "000000" , procentaje , false, false);
                                        paragraphdata7.setAlignment(ParagraphAlignment.CENTER);

                                        //CREAR SUBTABLA PARA PEP Y PARIENTE PEP ASOCIADOS

                                        XWPFTableRow rowt3 = t3.getRow(0); if (rowt3 == null) rowt3 = t3.createRow();
                                        XWPFParagraph paragrapht3 = rowt3.getCell(0).addParagraph();
                                        setRun(paragrapht3.createRun() , "Arial" , 6, "000000" , "PEP" , true, false);
                                        paragrapht3.setAlignment(ParagraphAlignment.CENTER);


                                    }
                                    //doc.insertTable(0, t2);
                                    //doc.insertTable(0, t2);
                                    p.getBody().insertTable(0, t2);
                                    p.getBody().insertTable(1, t3);

                                }
                                else if(sKey.equals("{tablaPep}"))
                                {
                                    text = text.replace(sKey, "");
                                    r.setText(text, 0);
                                }


                            }
                        }
                    }
                }
            }
        }

    }

}