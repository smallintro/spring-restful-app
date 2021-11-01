package io.github.smallintro.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.smallintro.springboot.entity.DefectInfo;

@Repository
public interface DefectRepo extends JpaRepository<DefectInfo, Integer> {

	@Query(value = "from DefectInfo where defectId = :defectId")
	DefectInfo findByDefectId(@Param("defectId") int defectId);
	
	@Query(value = "from DefectInfo where upper(developerName) = upper(:developerName)")
	List<DefectInfo> findByDeveloperName(@Param("developerName") String developerName);

	@Query(value = "from DefectInfo where upper(testerName) = upper(:testerName)")
	List<DefectInfo> findByTesterName(@Param("testerName") String testerName);

	@Query(value = "from DefectInfo where upper(defectStatus) = upper(:defectStatus)")
	List<DefectInfo> findByDefectStatus(@Param("defectStatus") String defectStatus);

}
