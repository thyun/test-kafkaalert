package com.skp.kafkaalert.process;

import com.skp.kafkaalert.config.ConfigScheme;

import lombok.Data;

@Data
public class ProcessScheme {
	ConfigScheme cscheme;

	public ProcessScheme(ConfigScheme cscheme) {
		this.cscheme = cscheme;
	}

}
