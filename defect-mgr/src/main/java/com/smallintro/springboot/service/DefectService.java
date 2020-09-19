package com.smallintro.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smallintro.springboot.entity.DefectInfo;
import com.smallintro.springboot.exception.RecordNotFoundException;
import com.smallintro.springboot.repository.DefectRepo;

@Service
public class DefectService {
	@Autowired
	private DefectRepo repo;

	public List<DefectInfo> findAllDefects() {
		return repo.findAll();
	}

	public DefectInfo findByDefectId(int defectId) throws RecordNotFoundException {
		DefectInfo defect = repo.findByDefectId(defectId);
		if (null==defect) {
			throw new RecordNotFoundException("No defect fount for the defect id "+defectId);
		}
		return defect;
	}

	public List<DefectInfo> findByTesterName(String testerName) {
		
		return repo.findByTesterName(testerName);
	}

	public List<DefectInfo> findByDeveloperName(String developerName) {
		return repo.findByDeveloperName(developerName);
	}

	public boolean isDefectExistsById(int defectId) {
		return repo.existsById(defectId);
	}

	public List<DefectInfo> findByDefectStatus(String defectStatus) {
		return repo.findByDefectStatus(defectStatus);
	}

	public void saveDefect(DefectInfo defect) {
		repo.save(defect);
	}

	public void deleteDefect(int defectId) throws RecordNotFoundException {
		if(!repo.existsById(defectId)) {
			throw new RecordNotFoundException("Not defect fount with defect id: "+defectId);
		}
		repo.deleteById(defectId);
		
	}
	
	

}
