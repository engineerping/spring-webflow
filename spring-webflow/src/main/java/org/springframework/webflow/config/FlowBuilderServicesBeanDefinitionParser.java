/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.config;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.util.StringUtils;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.expression.DefaultExpressionParserFactory;
import org.springframework.webflow.mvc.builder.MvcViewFactoryCreator;
import org.w3c.dom.Element;

/**
 * {@link BeanDefinitionParser} for the <code>&lt;flow-builder-services&gt;</code> tag.
 * 
 * @author Jeremy Grelle
 */
class FlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	protected Class getBeanClass(Element element) {
		return FlowBuilderServices.class;
	}

	protected void doParse(Element element, BeanDefinitionBuilder builder) {
		parseConversionService(element, builder);
		parseExpressionParser(element, builder);
		parseViewFactoryCreator(element, builder);
		parseDevelopment(element, builder);
	}

	private void parseConversionService(Element element, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute("conversion-service");
		if (StringUtils.hasText(conversionService)) {
			definitionBuilder.addPropertyReference("conversionService", conversionService);
		} else {
			definitionBuilder.addPropertyValue("conversionService", new DefaultConversionService());
		}
	}

	private void parseExpressionParser(Element element, BeanDefinitionBuilder definitionBuilder) {
		String expressionParser = element.getAttribute("expression-parser");
		if (StringUtils.hasText(expressionParser)) {
			definitionBuilder.addPropertyReference("expressionParser", expressionParser);
		} else {
			Object value = getConversionServiceValue(definitionBuilder);
			if (value instanceof RuntimeBeanReference) {
				BeanDefinitionBuilder builder = BeanDefinitionBuilder
						.genericBeanDefinition(DefaultExpressionParserFactory.class);
				builder.setFactoryMethod("getExpressionParser");
				builder.addConstructorArgValue(value);
				definitionBuilder.addPropertyValue("expressionParser", builder.getBeanDefinition());
			} else {
				ConversionService conversionService = (ConversionService) value;
				definitionBuilder.addPropertyValue("expressionParser", DefaultExpressionParserFactory
						.getExpressionParser(conversionService));
			}
		}
	}

	private Object getConversionServiceValue(BeanDefinitionBuilder definitionBuilder) {
		return definitionBuilder.getBeanDefinition().getPropertyValues().getPropertyValue("conversionService")
				.getValue();
	}

	private void parseViewFactoryCreator(Element element, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute("view-factory-creator");
		if (StringUtils.hasText(viewFactoryCreator)) {
			definitionBuilder.addPropertyReference("viewFactoryCreator", viewFactoryCreator);
		} else {
			definitionBuilder.addPropertyValue("viewFactoryCreator", BeanDefinitionBuilder.genericBeanDefinition(
					MvcViewFactoryCreator.class).getBeanDefinition());
		}
	}

	private void parseDevelopment(Element element, BeanDefinitionBuilder definitionBuilder) {
		String development = element.getAttribute("development");
		if (StringUtils.hasText(development)) {
			definitionBuilder.addPropertyValue("development", development);
		}
	}
}