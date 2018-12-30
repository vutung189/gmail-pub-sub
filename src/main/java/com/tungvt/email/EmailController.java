package com.tungvt.email;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

	@PostMapping("/email")
	public ResponseEntity registerAccount(HttpServletRequest request) throws IOException {

		System.out.println(IOUtils.toString(request.getReader()));
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
