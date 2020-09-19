package com.smallintro.springboot.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.smallintro.springboot.entity.DefectInfo;
import com.smallintro.springboot.exception.RecordNotFoundException;
import com.smallintro.springboot.service.DefectService;
import com.smallintro.springboot.utils.ApplicationProperties;
import com.smallintro.springboot.utils.ExcelReader;
import com.smallintro.springboot.utils.ExcelWriter;
import com.smallintro.springboot.utils.ProjectConstants;
import com.smallintro.springboot.utils.XMLReader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags="Defect management APIs",value="Defect Controller")
@RestController
@RequestMapping("defect")
@Validated
public class DefectController {

	@Autowired
	private ApplicationProperties appProperties;

	@Autowired
	private ExcelReader excelReader;

	@Autowired
	private ExcelWriter excelWriter;

	@Autowired
	private XMLReader xmlReader;

	@Autowired
	private DefectService defectService;

	@GetMapping
	public List<DefectInfo> getAllDefectDetails() {
		List<DefectInfo> defectDetails = defectService.findAllDefects();
		return defectDetails;
	}

	@GetMapping(value = "/io/export")
	public String exportDBToFile() {
		try {
			List<DefectInfo> defectDetails = defectService.findAllDefects();
			if (CollectionUtils.isEmpty(defectDetails)) {
				return ProjectConstants.NO_RECORD_FOUND;
			}
			excelWriter.writeDataToFile(defectDetails, ProjectConstants.DATA_TYPE_DEFECTS);

		} catch (Exception e) {
			return ProjectConstants.OPERATION_FAILED + " " + e.getCause();
		}
		return ProjectConstants.OPERATION_SUCCESS;
	}

	@PutMapping(value = "/io/import")
	public String importFileToDB() {
		try {
			excelReader.readExcelFile(ProjectConstants.DATA_TYPE_DEFECTS);

		} catch (Exception e) {
			return ProjectConstants.OPERATION_FAILED + " " + e.getCause();
		}
		return ProjectConstants.OPERATION_SUCCESS;
	}

	@ApiOperation(value="Query defect by defect Id")
	@GetMapping(value = "/{defectId}")
	public DefectInfo getDefectDetailByDefectId(@PathVariable int defectId) throws RecordNotFoundException {
		try {
			return defectService.findByDefectId(defectId);
		} catch (RecordNotFoundException e) {
			throw e;
		}

	}

	@GetMapping(value = "/tester/{testerName}")
	public List<DefectInfo> getDefectDetailByTesterName(@ApiParam(value="Tester's name")@PathVariable String testerName) {
		return defectService.findByTesterName(testerName);
	}

	@GetMapping(value = "/developer/{developerName}")
	public List<DefectInfo> getDefectDetailByDeveloperName(@ApiParam(value="Developer's name")@PathVariable String developerName) {
		return defectService.findByDeveloperName(developerName);
	}

	@GetMapping(value = "/status/{defectStatus}")
	public List<DefectInfo> getDefectDetail(@ApiParam(value="Defect status")@PathVariable String defectStatus) {
		return defectService.findByDefectStatus(defectStatus);
	}

	@PostMapping
	public ResponseEntity<Void> addDefect(@Valid @RequestBody DefectInfo defect, UriComponentsBuilder builder) {
		if (defect.getDefectId() > 0 && defectService.isDefectExistsById(defect.getDefectId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					" A defect with id " + defect.getDefectId() + " already exists");
		}
		defectService.saveDefect(defect);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(builder.path("defect/{id}").buildAndExpand(defect.getDefectId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@PutMapping(value = "/{defectId}")
	public String updateDefect(@PathVariable int defectId, @RequestBody DefectInfo defect) {
		if (defectService.isDefectExistsById(defectId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					" No defect found with defect id " + defect.getDefectId());
		}
		defectService.saveDefect(defect);
		return ProjectConstants.OPERATION_SUCCESS;
	}

	@DeleteMapping(value = "/{defectId}")
	public String delDefectById(@PathVariable int defectId) {
		try {
			defectService.deleteDefect(defectId);
		} catch (RecordNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		return ProjectConstants.OPERATION_SUCCESS;
	}

	@GetMapping(value = "/model/{modelName}")
	public String readModelConfig(@PathVariable String modelName) {

		try {
			System.out.println(appProperties.toString());
			xmlReader.readModelConfig(modelName);
			return ProjectConstants.OPERATION_SUCCESS + " for model= " + modelName;

		} catch (Exception e) {
			return ProjectConstants.OPERATION_FAILED;
		}
	}

	/*@RequestMapping("/*")
	public String handleError() {
		return ProjectConstants.OPERATION_FAILED + ": Error in " + this.getClass().getName();
	}*/
}
