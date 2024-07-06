package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.dto.BodyCategory;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.ExcelCarRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.model.CarFields;
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
import java.util.Optional;

@Service
public class ExcelProcessorService {

    public List<ExcelCarRequest> extractDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            return getValuesFromSheet(sheet);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e.getMessage());
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
        Optional<Picture> optionalCarPicture = getCarPicture(sheetPictures, currentRow);

        Iterator<Cell> cellIterator = currentRow.cellIterator();
        List<Object> values = new ArrayList<>();

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            switch (cell.getCellType()) {
                case STRING -> values.add(cell.getStringCellValue());
                case NUMERIC -> values.add(dataFormatter.formatCellValue(cell));
                default -> throw new SwiftWheelsHubException("Unknown Excel cell type");
            }

            Optional<Picture> optionalPictureFiltered =
                    optionalCarPicture.filter(picture -> cell.getColumnIndex() + 1 == picture.getClientAnchor().getCol1());

            if (optionalPictureFiltered.isPresent()) {
                Picture picture = optionalPictureFiltered.get();
                values.add(picture.getPictureData());
            }
        }

        return values;
    }

    private List<Picture> getSheetPictures(Sheet sheet) {
        return ((XSSFSheet) sheet).createDrawingPatriarch()
                .getShapes()
                .stream()
                .filter(xssfShape -> xssfShape instanceof Picture)
                .map(xssfShape -> ((Picture) xssfShape))
                .toList();
    }

    private Optional<Picture> getCarPicture(List<Picture> sheetPictures, Row currentRow) {
        return sheetPictures.stream()
                .filter(picture -> currentRow.getRowNum() == picture.getClientAnchor().getRow1())
                .findFirst();
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
