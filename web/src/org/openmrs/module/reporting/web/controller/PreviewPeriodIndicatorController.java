package org.openmrs.module.reporting.web.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.evaluation.EvaluationContext;
import org.openmrs.module.evaluation.parameter.Parameterizable;
import org.openmrs.module.indicator.Indicator;
import org.openmrs.module.indicator.IndicatorResult;
import org.openmrs.module.indicator.PeriodCohortIndicator;
import org.openmrs.module.indicator.service.IndicatorService;
import org.openmrs.module.reporting.web.model.IndicatorForm;
import org.openmrs.module.util.ParameterizableUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("indicatorForm")
public class PreviewPeriodIndicatorController {

	/* Logger */
	private Log log = LogFactory.getLog(this.getClass());

	/* Date format */
	private DateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Allows us to bind a custom editor for a class.
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(ymd, true));
	}
	
	/**
	 * Populates the form backing object for the 
	 * 
	 * @param uuid
	 * @param className
	 * @return
	 */
	@ModelAttribute("indicatorForm")
	public IndicatorForm formBackingObject(	
			@RequestParam(value = "uuid", required=false) String uuid) { 
		
		IndicatorForm form = new IndicatorForm();
	
		Indicator indicator = Context.getService(IndicatorService.class).getIndicatorByUuid(uuid);
		log.info("indicator = " + indicator);

		// If indicator does not exist, we just create a new one
		if (indicator != null) { 
			//if (!indicator.getClass().isAssignableFrom(PeriodCohortIndicator.class)) 
			form.setCohortIndicator((PeriodCohortIndicator)indicator);
		} 
		// Otherwise, we populate the form bean with the indicator
		else {			
			form.setCohortIndicator(new PeriodCohortIndicator());			
		}		
		return form;
	}
	
	/**
	 * Sets up the form for previewing a period indicator.
	 * 
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value="/module/reporting/indicators/previewPeriodIndicator", method=RequestMethod.GET)
	public ModelAndView showForm() { 				
		ModelAndView model = new ModelAndView("/module/reporting/indicators/previewPeriodIndicator");
		// Nothing to do right now except forward to the JSP
		return model;
	}	
	
	/**
	 * Processes the evaluation of the period indicator.
	 * 
	 * @param cohortDefinition
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value="/module/reporting/indicators/previewPeriodIndicator",method = RequestMethod.POST)
	public ModelAndView processForm(
		@ModelAttribute("indicatorForm") IndicatorForm form,
		BindingResult bindingResult) {
		
		log.info("POST /module/reporting/indicators/previewPeriodIndicator");
		
		if (bindingResult.hasErrors()) {
			return showForm();
		}
		
		ModelAndView model = new ModelAndView("/module/reporting/indicators/previewPeriodIndicator");

		if (form != null) { 
			log.info("Evaluating period indicator ");
			EvaluationContext context = new EvaluationContext();
			
			Map<String, Object> parameterValues = 
				ParameterizableUtil.getParameterValues(form.getCohortIndicator(), form.getParameterValues());
			context.setParameterValues(parameterValues);
			
			IndicatorResult indicatorResult = 
				Context.getService(IndicatorService.class).evaluate(form.getCohortIndicator(), context);

			log.info("indicatorResult: " + indicatorResult);		
			model.addObject("indicatorResult", indicatorResult);
			
		}			
		return model;
		
		//return new ModelAndView("redirect:/module/reporting/closeWindow.htm");
	}
			
	
	
	


}