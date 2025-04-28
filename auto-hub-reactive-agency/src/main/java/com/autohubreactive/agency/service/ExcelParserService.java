package com.autohubreactive.agency.service;

import com.autohubreactive.dto.BodyCategory;
import com.autohubreactive.dto.CarState;
import com.autohubreactive.dto.ExcelCarRequest;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.model.CarFields;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParserService {

    public List<ExcelCarRequest> extractDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            return getValuesFromSheet(sheet);
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }
    }

    private List<ExcelCarRequest> getValuesFromSheet(Sheet sheet) {
        DataFormatter dataFormatter = new DataFormatter();
        List<Picture> sheetPictures = getSheetPictures(sheet);
        List<ExcelCarRequest> excelCarRequests = new ArrayList<>();

        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row currentRow = sheet.getRow(rowIndex);
            List<Object> values = getCellValues(currentRow, sheetPictures, dataFormatter);

            excelCarRequests.add(generateExcelCarRequest(values));
        }

        return Collections.unmodifiableList(excelCarRequests);
    }

    private List<Object> getCellValues(Row currentRow, List<Picture> sheetPictures, DataFormatter dataFormatter) {
        Iterator<Cell> cellIterator = currentRow.cellIterator();
        List<Object> values = new ArrayList<>();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            switch (cell.getCellType()) {
                case STRING -> values.add(cell.getStringCellValue());
                case NUMERIC -> values.add(dataFormatter.formatCellValue(cell));
                default -> throw new AutoHubException("Unknown Excel cell type");
            }
        }

        values.add(getCarPictureData(sheetPictures, currentRow));

        return Collections.unmodifiableList(values);
    }

    private List<Picture> getSheetPictures(Sheet sheet) {
        return ((XSSFSheet) sheet).createDrawingPatriarch()
                .getShapes()
                .stream()
                .filter(xssfShape -> xssfShape instanceof Picture)
                .map(xssfShape -> ((Picture) xssfShape))
                .toList();
    }

    private PictureData getCarPictureData(List<Picture> sheetPictures, Row currentRow) {
        return sheetPictures.stream()
                .filter(picture -> hasCorrespondingRowAndColumn(currentRow, picture))
                .findFirst()
                .map(Picture::getPictureData)
                .orElse(null);
    }

    private boolean hasCorrespondingRowAndColumn(Row currentRow, Picture picture) {
        return currentRow.getRowNum() == picture.getClientAnchor().getRow1() &&
                currentRow.getLastCellNum() == picture.getClientAnchor().getCol1();
    }

    private ExcelCarRequest generateExcelCarRequest(List<Object> values) {
        return ExcelCarRequest.builder()
                .make((String) values.get(CarFields.MAKE.ordinal()))
                .model((String) values.get(CarFields.MODEL.ordinal()))
                .bodyCategory(BodyCategory.valueOf(((String) values.get(CarFields.BODY_TYPE.ordinal())).toUpperCase()))
                .yearOfProduction(Integer.parseInt((String) values.get(CarFields.YEAR_OF_PRODUCTION.ordinal())))
                .color((String) values.get(CarFields.COLOR.ordinal()))
                .mileage(Integer.parseInt((String) values.get(CarFields.MILEAGE.ordinal())))
                .carState(CarState.valueOf(((String) values.get(CarFields.CAR_STATUS.ordinal())).toUpperCase()))
                .amount(new BigDecimal((String) values.get(CarFields.AMOUNT.ordinal())))
                .originalBranchId((String) values.get(CarFields.ORIGINAL_BRANCH.ordinal()))
                .actualBranchId((String) values.get(CarFields.ACTUAL_BRANCH.ordinal()))
                .image(getImageData((PictureData) values.get(CarFields.IMAGE.ordinal())))
                .build();
    }

    private byte[] getImageData(PictureData pictureData) {
        return ObjectUtils.isEmpty(pictureData) ? null : pictureData.getData();
    }

}
