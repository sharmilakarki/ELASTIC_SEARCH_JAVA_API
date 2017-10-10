package com.sharmila.musiclibrary.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sharmila.musiclibrary.api.RandomManager;

@RestController
@RequestMapping("/random")
public class RandomController {

	@Autowired
	private RandomManager randomManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Map<String,String>> search(){
		
		List<Map<String, String>> result = randomManager.searchAll();
		
		return result;
		
	}
}
