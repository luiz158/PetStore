package com.example.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.bo.PetBO;
import com.example.bo.PetStatusType;
import com.example.bo.TagBO;
import com.example.dto.NamedObjectDTO;
import com.example.dto.PetDTO;
import com.example.repository.PetRepository;
import com.example.repository.TagRepository;
import com.example.service.PetService;
import com.example.transform.NamedObjectTransformer;
import com.example.transform.PetTransformer;

@Service
@Transactional
public class PetServiceImpl implements PetService {

	@Autowired
	private PetRepository petRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PetTransformer petTransformer;

	@Autowired
	private NamedObjectTransformer namedObjectTransformer;

	@Override
	public PetDTO getPet(long petId) {
		return petTransformer.toDTO(petRepository.findOne(petId));
	}

	@Override
	public boolean addPet(PetDTO petDTO) {
		if (petDTO == null) {
			return false;
		}

		petDTO.getTags().forEach(tagName -> {
			if (tagRepository.findByName(tagName) == null) {
				TagBO tagBO = new TagBO();
				tagBO.setName(tagName);
				tagRepository.save(tagBO);
			}
		});

		PetBO petBO = petTransformer.toBO(petDTO);
		if (petBO.getCategory() == null) {
			return false;
		}

		petBO.getCategory().getPets().add(petBO);
		petBO.getTags().forEach(tagBO -> {
			tagBO.getPets().add(petBO);
		});

		petRepository.save(petBO);
		return true;
	}

	@Override
	public boolean deletePet(long petId) {
		if (!petRepository.exists(petId)) {
			return false;
		}
		petRepository.delete(petId);
		return true;
	}

	@Override
	public List<NamedObjectDTO> getPetStatuses() {
		return namedObjectTransformer.toDTOList(Arrays.asList(PetStatusType.values()));
	}

}
