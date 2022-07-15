package org.grits.toolbox.tools.databasebot.excel;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
import org.grits.toolbox.tools.databasebot.excel.om.CompositionInformation;
import org.grits.toolbox.tools.databasebot.excel.om.MassInformation;
import org.grits.toolbox.tools.databasebot.om.GlycanInformation;
import org.grits.toolbox.tools.databasebot.utils.ComperatorCompositionInformationMass;
import org.grits.toolbox.tools.databasebot.utils.ComperatorGlycanInformationMass;
import org.grits.toolbox.tools.databasebot.utils.ComperatorMassInformationMass;
import org.grits.toolbox.tools.databasebot.utils.InformationGenerator;
import org.grits.toolbox.utils.io.ExcelWriterHelper;

public class ReportGenerator
{
    private static double NA_MASS = 22.98977D;

    private Double m_imageScalingFactor = 0.5D;
    private List<Picture> m_images = new ArrayList<>();
//    private GlycanWorkspace m_gwb = new GlycanWorkspace(null, false, new GlycanRendererAWT());
    private HSSFWorkbook m_workbook = null;
    private Integer m_currentRow = 0;
    // private GlycanImageProvider m_imageProvider = new GlycanImageProvider();
    private ExcelWriterHelper m_imageWriterHelper = new ExcelWriterHelper();

    public ReportGenerator()
    {
        /*
         * CartoonOptions t_options = new
         * CartoonOptions(GraphicOptions.NOTATION_CFG,
         * GraphicOptions.DISPLAY_NORMALINFO, IMAGE_SCALING_FACTOR,
         * GraphicOptions.RL, false, false, true);
         * this.m_imageProvider.setCartoonOptions(t_options);
         */
    }

    private void clear()
    {
        this.m_images.clear();
        this.m_currentRow = 1;
    }

    public void write(String a_fileName, List<GlycanInformation> a_glycans)
            throws SugarImporterException, GlycoVisitorException, IOException
    {
        this.m_workbook = new HSSFWorkbook();
        // Structure List - all that passed filter
        this.addGlycanList("Structure List", a_glycans, Boolean.TRUE);
        resizeImages();
        // Composition List
        List<CompositionInformation> t_compositions = this.createCompositionView(a_glycans);
        this.addCompositionList("Composition List", t_compositions);
        // mass list
        List<MassInformation> t_massList = this.findMasses(a_glycans, 100D);
        this.addMassList("Mass List", t_massList, true, GraphicOptions.DISPLAY_NORMALINFO);
        resizeImages();
        // write filtered out Structures
        this.addGlycanList("Filtered out", a_glycans, Boolean.FALSE);
        resizeImages();
        // write the file
        FileOutputStream t_fos = new FileOutputStream(a_fileName);
        this.m_workbook.write(t_fos);
        t_fos.flush();
        t_fos.close();
    }
    
    private void resizeImages () {
    	// resize images to prevent stretching
        for (Picture t_picture : this.m_images)
        {
            t_picture.resize();
        }
    }

    public void writeTopology(String a_fileName, List<GlycanInformation> a_glycans,
            List<GlycanInformation> a_glycansFilterData) throws IOException
    {
        this.m_workbook = new HSSFWorkbook();
        // Structure List - all that passed filter
        this.addGlycanList("Structure List", a_glycans, Boolean.TRUE);
        resizeImages();
        // Composition List
        List<CompositionInformation> t_compositions = this.createCompositionView(a_glycans);
        this.addCompositionList("Composition List", t_compositions);
        // mass list
        List<MassInformation> t_massList = this.findMasses(a_glycans, 100D);
        this.addMassList("Mass List", t_massList, true, GraphicOptions.DISPLAY_NORMALINFO);
        resizeImages();
        // write filtered out Structures
        this.addGlycanList("Filtered out", a_glycansFilterData, Boolean.FALSE);
        resizeImages();
        // add topology page
        this.addTopologyList("Topology List", a_glycans, GraphicOptions.DISPLAY_COMPACT);
        resizeImages();
        // write the file
        FileOutputStream t_fos = new FileOutputStream(a_fileName);
        this.m_workbook.write(t_fos);
        t_fos.flush();
        t_fos.close();
    }

    private void addTopologyList(String a_name, List<GlycanInformation> a_glycans, String a_displayStyle)
    {
        this.clear();
        this.m_images = new ArrayList<>();
        HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
        this.initColumnsMassList(t_sheetStructures);
        Collections.sort(a_glycans, new ComperatorGlycanInformationMass());
        for (GlycanInformation t_info : a_glycans)
        {
            this.addTopologyRow(t_info, t_sheetStructures, a_displayStyle);
        }
    }

    private void addTopologyRow(GlycanInformation a_info, HSSFSheet a_sheet, String a_displayStyle)
    {
        Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
        // mass
        Cell t_cell = t_rowStructure.createCell(0);
        t_cell.setCellValue(a_info.getMass());
        t_cell.setCellType(CellType.NUMERIC);
        // mass pMe
        t_cell = t_rowStructure.createCell(1);
        t_cell.setCellValue(a_info.getMassPme());
        t_cell.setCellType(CellType.NUMERIC);
        // pMe + Na
        t_cell = t_rowStructure.createCell(2);
        t_cell.setCellValue(a_info.getMassPme() + ReportGenerator.NA_MASS);
        t_cell.setCellType(CellType.NUMERIC);
        // pMed + 2Na
        t_cell = t_rowStructure.createCell(3);
        t_cell.setCellValue((a_info.getMassPme() + ReportGenerator.NA_MASS + ReportGenerator.NA_MASS) / 2D);
        t_cell.setCellType(CellType.NUMERIC);
        // # Structures
        t_cell = t_rowStructure.createCell(4);
        t_cell.setCellValue(a_info.getTopologyOrigin().size());
        t_cell.setCellType(CellType.NUMERIC);
        // image
        int t_column = 5;
        for (GlycanInformation t_glycanInfo : a_info.getTopologyOrigin())
        {
            Row t_rowStructureImage = null;
            try
            {
                // GlycanImageObject t_image =
                // this.createImage(t_glycanInfo.getGwb(), a_displayStyle);
                t_rowStructureImage = t_rowStructure;
                // BufferedImage img = this.createImage(t_glycanInfo.getGwb(), a_displayStyle);
                BufferedImage img = this.m_imageWriterHelper.createGlycanImage(t_glycanInfo.getGwb(),
                        a_displayStyle, false, false, this.m_imageScalingFactor);
                this.m_imageWriterHelper.writeCellImage(this.m_workbook, a_sheet, this.m_currentRow, t_column, img,
                        this.m_images);
            }
            catch (Exception e)
            {
                t_cell = t_rowStructureImage.createCell(t_column);
                t_cell.setCellValue(e.getMessage());
                t_cell.setCellType(CellType.STRING);
            }
            t_column++;
        }
        this.m_currentRow += 1;
    }

    private void addMassList(String a_name, List<MassInformation> a_massList, boolean a_withId, String a_displayStyle)
    {
        this.clear();
        this.m_images = new ArrayList<>();
        HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
        this.initColumnsMassList(t_sheetStructures);
        Collections.sort(a_massList, new ComperatorMassInformationMass());
        for (MassInformation t_info : a_massList)
        {
            this.addMassRows(t_info, t_sheetStructures, a_withId, a_displayStyle);
        }
    }

    private void addMassRows(MassInformation a_info, HSSFSheet a_sheet, boolean a_withId, String a_displayStyle)
    {
        Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
        // mass
        Cell t_cell = t_rowStructure.createCell(0);
        t_cell.setCellValue(a_info.getMass());
        t_cell.setCellType(CellType.NUMERIC);
        // mass pMe
        t_cell = t_rowStructure.createCell(1);
        t_cell.setCellValue(a_info.getMassPme());
        t_cell.setCellType(CellType.NUMERIC);
        // pMe + Na
        t_cell = t_rowStructure.createCell(2);
        t_cell.setCellValue(a_info.getMassPme() + ReportGenerator.NA_MASS);
        t_cell.setCellType(CellType.NUMERIC);
        // pMed + 2Na
        t_cell = t_rowStructure.createCell(3);
        t_cell.setCellValue((a_info.getMassPme() + ReportGenerator.NA_MASS + ReportGenerator.NA_MASS) / 2D);
        t_cell.setCellType(CellType.NUMERIC);
        // # Structures
        t_cell = t_rowStructure.createCell(4);
        t_cell.setCellValue(a_info.getMembers().size());
        t_cell.setCellType(CellType.NUMERIC);
        // image
        int t_column = 5;
        for (GlycanInformation t_glycanInfo : a_info.getMembers())
        {
            Row t_rowStructureImage = null;
            try
            {
                // GlycanImageObject t_image =
                // this.createImage(t_glycanInfo.getGwb(), a_displayStyle);
                // BufferedImage img = this.createImage(t_glycanInfo.getGwb(), a_displayStyle);
                BufferedImage img = this.m_imageWriterHelper.createGlycanImage(t_glycanInfo.getGwb(),
                        a_displayStyle, false, false, this.m_imageScalingFactor);
                if (a_withId)
                {
                    t_cell = t_rowStructure.createCell(t_column);
                    t_cell.setCellValue(t_glycanInfo.getId());
                    t_cell.setCellType(CellType.STRING);
                    t_rowStructureImage = a_sheet.createRow(this.m_currentRow + 1);
                    this.m_imageWriterHelper.writeCellImage(this.m_workbook, a_sheet, this.m_currentRow + 1, t_column,
                            img, this.m_images);
                }
                else
                {
                    t_rowStructureImage = t_rowStructure;
                    this.m_imageWriterHelper.writeCellImage(this.m_workbook, a_sheet, this.m_currentRow, t_column, img,
                            this.m_images);
                }
            }
            catch (Exception e)
            {
                t_cell = t_rowStructureImage.createCell(t_column);
                t_cell.setCellValue(e.getMessage());
                t_cell.setCellType(CellType.STRING);
            }
            t_column++;
        }
        if (a_withId)
        {
            this.m_currentRow += 2;
        }
        else
        {
            this.m_currentRow += 1;
        }
    }

    private void initColumnsMassList(HSSFSheet a_sheet)
    {
        Row t_row = a_sheet.createRow(0);

        Cell t_cell = t_row.createCell(0);
        t_cell.setCellValue("Native mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(0, 3000);

        t_cell = t_row.createCell(1);
        t_cell.setCellValue("PMe mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(1, 3000);

        t_cell = t_row.createCell(2);
        t_cell.setCellValue("PMe Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(2, 3000);

        t_cell = t_row.createCell(3);
        t_cell.setCellValue("PMe 2Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(3, 3000);

        t_cell = t_row.createCell(4);
        t_cell.setCellValue("# Structures");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(3, 3000);
    }

    private List<MassInformation> findMasses(List<GlycanInformation> a_glycans, Double a_deviationPPM)
    {
        List<MassInformation> t_result = new ArrayList<>();
        for (GlycanInformation t_glycanInfo : a_glycans)
        {
            if (t_glycanInfo.getPassFilter())
            {
                MassInformation t_info = this.assignToMassInfo(t_glycanInfo, t_result, a_deviationPPM);
                // new mass info created?
                if (t_info != null)
                {
                    t_result.add(t_info);
                }
            }
        }
        return t_result;
    }

    private MassInformation assignToMassInfo(GlycanInformation a_glycanInfo, List<MassInformation> a_result,
            Double a_defiationPPM)
    {
        for (MassInformation t_massInformation : a_result)
        {
            if (t_massInformation.isInRange(a_glycanInfo.getMass()))
            {
                t_massInformation.addMember(a_glycanInfo);
                return null;
            }
        }
        MassInformation t_information = new MassInformation(a_glycanInfo.getMass(), a_defiationPPM);
        t_information.setMassPme(a_glycanInfo.getMassPme());
        t_information.addMember(a_glycanInfo);
        return t_information;
    }

    private void addCompositionList(String a_name, List<CompositionInformation> a_compositions)
    {
        this.clear();
        HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
        HashMap<String, Integer> t_compositionColumns = this.initColumnsCompositionList(t_sheetStructures,
                a_compositions);
        Collections.sort(a_compositions, new ComperatorCompositionInformationMass());
        for (CompositionInformation t_info : a_compositions)
        {
            this.addCompositionRows(t_info, t_sheetStructures, t_compositionColumns);
        }
    }

    private void addCompositionRows(CompositionInformation a_info, HSSFSheet a_sheet,
            HashMap<String, Integer> a_compositionColumns)
    {
        Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
        // mass
        Cell t_cell = t_rowStructure.createCell(0);
        t_cell.setCellValue(a_info.getMass());
        t_cell.setCellType(CellType.NUMERIC);
        // mass pMe
        t_cell = t_rowStructure.createCell(1);
        t_cell.setCellValue(a_info.getMassPme());
        t_cell.setCellType(CellType.NUMERIC);
        // pMe + Na
        t_cell = t_rowStructure.createCell(2);
        t_cell.setCellValue(a_info.getMassPme() + ReportGenerator.NA_MASS);
        t_cell.setCellType(CellType.NUMERIC);
        // pMed + 2Na
        t_cell = t_rowStructure.createCell(3);
        t_cell.setCellValue((a_info.getMassPme() + ReportGenerator.NA_MASS + ReportGenerator.NA_MASS) / 2D);
        t_cell.setCellType(CellType.NUMERIC);
        // composition
        t_cell = t_rowStructure.createCell(4);
        t_cell.setCellValue(a_info.getCompositionName());
        t_cell.setCellType(CellType.STRING);
        // Number of structures
        t_cell = t_rowStructure.createCell(5);
        t_cell.setCellValue(a_info.getMembers().size());
        t_cell.setCellType(CellType.NUMERIC);
        // composition
        HashMap<String, Integer> t_compostionHash = a_info.getComposition();
        for (String t_component : t_compostionHash.keySet())
        {
            t_cell = t_rowStructure.createCell(a_compositionColumns.get(t_component));
            t_cell.setCellValue(t_compostionHash.get(t_component));
            t_cell.setCellType(CellType.NUMERIC);
        }
        this.m_currentRow++;
    }

    private HashMap<String, Integer> initColumnsCompositionList(HSSFSheet a_sheet,
            List<CompositionInformation> a_glycans)
    {
        Row t_row = a_sheet.createRow(0);

        Cell t_cell = t_row.createCell(0);
        t_cell.setCellValue("Native mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(0, 3000);

        t_cell = t_row.createCell(1);
        t_cell.setCellValue("PMe mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(1, 3000);

        t_cell = t_row.createCell(2);
        t_cell.setCellValue("PMe Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(2, 3000);

        t_cell = t_row.createCell(3);
        t_cell.setCellValue("PMe 2Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(3, 3000);

        t_cell = t_row.createCell(4);
        t_cell.setCellValue("Composition");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(4, 12000);

        t_cell = t_row.createCell(5);
        t_cell.setCellValue("# Structures");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(5, 3000);

        HashMap<String, Integer> t_compColComposition = this.createCompositionColumnFromComposition(5, a_glycans);

        for (String t_component : t_compColComposition.keySet())
        {
            t_cell = t_row.createCell(t_compColComposition.get(t_component));
            t_cell.setCellValue(InformationGenerator.formatComposition(t_component));
            t_cell.setCellType(CellType.STRING);
        }
        return t_compColComposition;
    }

    private List<CompositionInformation> createCompositionView(List<GlycanInformation> a_glycans)
    {
        HashMap<String, CompositionInformation> t_compositionView = new HashMap<>();
        for (GlycanInformation t_glycanInformation : a_glycans)
        {
            if (t_glycanInformation.getPassFilter())
            {
                CompositionInformation t_collection = t_compositionView.get(t_glycanInformation.getCompositionName());
                if (t_collection == null)
                {
                    t_collection = new CompositionInformation();
                    t_collection.setComposition(t_glycanInformation.getComposition());
                    t_collection.setCompositionString(t_glycanInformation.getCompositionName());
                    t_collection.setMass(t_glycanInformation.getMass());
                    t_collection.setMassPme(t_glycanInformation.getMassPme());
                    t_compositionView.put(t_glycanInformation.getCompositionName(), t_collection);
                }
                t_collection.addMember(t_glycanInformation);
            }
        }
        List<CompositionInformation> t_result = new ArrayList<>();
        for (String t_composition : t_compositionView.keySet())
        {
            t_result.add(t_compositionView.get(t_composition));
        }
        return t_result;
    }

    private void addGlycanList(String a_name, List<GlycanInformation> a_glycans, Boolean a_filterSetting)
    {
        this.clear();
        HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
        HashMap<String, Integer> t_compositionColumns = this.initColumnsGlycanList(t_sheetStructures, a_glycans);
        Collections.sort(a_glycans, new ComperatorGlycanInformationMass());
        for (GlycanInformation t_info : a_glycans)
        {
            if (t_info.getPassFilter().equals(a_filterSetting))
            {
                this.addGlycanRows(t_info, t_sheetStructures, t_compositionColumns);
            }
        }
    }

    private void addGlycanRows(GlycanInformation a_info, HSSFSheet a_sheet,
            HashMap<String, Integer> a_compositionColumns)
    {
        Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
        // mass
        Cell t_cell = t_rowStructure.createCell(0);
        t_cell.setCellValue(a_info.getMass());
        t_cell.setCellType(CellType.NUMERIC);
        // mass pMe
        t_cell = t_rowStructure.createCell(1);
        t_cell.setCellValue(a_info.getMassPme());
        t_cell.setCellType(CellType.NUMERIC);
        // pMe + Na
        t_cell = t_rowStructure.createCell(2);
        t_cell.setCellValue(a_info.getMassPme() + ReportGenerator.NA_MASS);
        t_cell.setCellType(CellType.NUMERIC);
        // pMed + 2Na
        t_cell = t_rowStructure.createCell(3);
        t_cell.setCellValue((a_info.getMassPme() + ReportGenerator.NA_MASS + ReportGenerator.NA_MASS) / 2D);
        t_cell.setCellType(CellType.NUMERIC);
        // composition
        t_cell = t_rowStructure.createCell(4);
        t_cell.setCellValue(a_info.getCompositionName());
        t_cell.setCellType(CellType.STRING);
        // glycan ID
        t_cell = t_rowStructure.createCell(5);
        t_cell.setCellValue(a_info.getId());
        t_cell.setCellType(CellType.STRING);
        // glytoucan ID
        t_cell = t_rowStructure.createCell(6);
        t_cell.setCellValue(a_info.getGlytoucanId());
        t_cell.setCellType(CellType.STRING);
        // image
        try
        {
            // GlycanImageObject t_image = this.createImage(a_info.getGwb(),
            // GraphicOptions.DISPLAY_NORMALINFO);
            // BufferedImage img = this.createImage(a_info.getGwb(), GraphicOptions.DISPLAY_NORMALINFO);
            BufferedImage img = this.m_imageWriterHelper.createGlycanImage(a_info.getGwb(),
                    GraphicOptions.DISPLAY_NORMALINFO, false, false, this.m_imageScalingFactor);
            this.m_imageWriterHelper.writeCellImage(this.m_workbook, a_sheet, this.m_currentRow, 7, img, this.m_images);
        }
        catch (Exception e)
        {
            t_cell = t_rowStructure.createCell(7);
            t_cell.setCellValue(e.getMessage());
            t_cell.setCellType(CellType.STRING);
        }
        // composition
        HashMap<String, Integer> t_compostionHash = a_info.getComposition();
        for (String t_component : t_compostionHash.keySet())
        {
            t_cell = t_rowStructure.createCell(a_compositionColumns.get(t_component));
            t_cell.setCellValue(t_compostionHash.get(t_component));
            t_cell.setCellType(CellType.NUMERIC);
        }
        this.m_currentRow++;
    }

//    private BufferedImage createImage(String a_sequence, String a_displayStyle) throws Exception
//    {
//        this.m_gwb.setDisplay(a_displayStyle);
//        // GWB has a own scaling - here set to 1 to not use it. The reason is
//        // that GWB tries to be smart and for 75% image size removes the
//        // linkages. Which is in this case unwanted.
//        double dScaleTo =  this.m_imageScalingFactor < 1.0 ? 2.0 : this.m_imageScalingFactor;
//		double dScaleFrom = this.m_imageScalingFactor < 1.0 ? this.m_imageScalingFactor / 2.0 : this.m_imageScalingFactor;
//        BufferedImage img = this.m_gwb.getGlycanRenderer().getImage(new Union<>(Glycan.fromString(a_sequence)), true,
//                false, false, dScaleTo);
//        // Now we do our own scaling
//        if (this.m_imageScalingFactor < 1.0D)
//        {
//            int width = (int) (img.getWidth() * dScaleFrom);
//            int height = (int) (img.getHeight() * dScaleFrom);
//            java.awt.Image newImage = img.getScaledInstance(width, height, BufferedImage.SCALE_AREA_AVERAGING);
//            BufferedImage newBufferedImage = SimianImageConverterOld.convert(newImage);
//            newImage.flush();
//            return newBufferedImage;
//        }
//        return img;
//    }

    private HashMap<String, Integer> initColumnsGlycanList(HSSFSheet a_sheet, List<GlycanInformation> a_glycans)
    {
        Row t_row = a_sheet.createRow(0);

        Cell t_cell = t_row.createCell(0);
        t_cell.setCellValue("Native mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(0, 3000);

        t_cell = t_row.createCell(1);
        t_cell.setCellValue("PMe mass");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(1, 3000);

        t_cell = t_row.createCell(2);
        t_cell.setCellValue("PMe Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(2, 3000);

        t_cell = t_row.createCell(3);
        t_cell.setCellValue("PMe 2Na+");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(3, 3000);

        t_cell = t_row.createCell(4);
        t_cell.setCellValue("Composition");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(4, 12000);

        t_cell = t_row.createCell(5);
        t_cell.setCellValue("GlycO ID");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(5, 3000);
        
        t_cell = t_row.createCell(6);
        t_cell.setCellValue("GlyTouCan ID");
        t_cell.setCellType(CellType.STRING);
        a_sheet.setColumnWidth(6, 3000);

        t_cell = t_row.createCell(7);
        t_cell.setCellValue("Cartoon");
        t_cell.setCellType(CellType.STRING);

        HashMap<String, Integer> t_compColComposition = this.createCompositionColumnFromGlycan(7, a_glycans);

        for (String t_component : t_compColComposition.keySet())
        {
            t_cell = t_row.createCell(t_compColComposition.get(t_component));
            t_cell.setCellValue(InformationGenerator.formatComposition(t_component));
            t_cell.setCellType(CellType.STRING);
        }
        return t_compColComposition;
    }

    private HashMap<String, Integer> createCompositionColumnFromGlycan(int a_startPosition,
            List<GlycanInformation> a_glycans)
    {
        ArrayList<String> t_components = new ArrayList<>();
        HashMap<String, Integer> t_result = new HashMap<>();
        for (GlycanInformation t_info : a_glycans)
        {
            HashMap<String, Integer> t_compostion = t_info.getComposition();
            for (String t_comp : t_compostion.keySet())
            {
                if (t_result.get(t_comp) == null)
                {
                    t_result.put(t_comp, 0);
                    t_components.add(t_comp);
                }
            }
        }
        Collections.sort(t_components);
        int t_position = a_startPosition + 1;
        for (String t_string : t_components)
        {
            t_result.put(t_string, t_position);
            t_position++;
        }
        return t_result;
    }

    private HashMap<String, Integer> createCompositionColumnFromComposition(int a_startPosition,
            List<CompositionInformation> a_glycans)
    {
        ArrayList<String> t_components = new ArrayList<>();
        HashMap<String, Integer> t_result = new HashMap<>();
        for (CompositionInformation t_info : a_glycans)
        {
            HashMap<String, Integer> t_compostion = t_info.getComposition();
            for (String t_comp : t_compostion.keySet())
            {
                if (t_result.get(t_comp) == null)
                {
                    t_result.put(t_comp, 0);
                    t_components.add(t_comp);
                }
            }
        }
        Collections.sort(t_components);
        int t_position = a_startPosition + 1;
        for (String t_string : t_components)
        {
            t_result.put(t_string, t_position);
            t_position++;
        }
        return t_result;
    }

    public Double getImageScalingFactor()
    {
        return this.m_imageScalingFactor;
    }

    public void setImageScalingFactor(Double a_imageScalingFactor)
    {
        this.m_imageScalingFactor = a_imageScalingFactor;
    }

}
