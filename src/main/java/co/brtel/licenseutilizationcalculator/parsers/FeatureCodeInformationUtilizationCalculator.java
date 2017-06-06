package co.brtel.licenseutilizationcalculator.parsers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.brtel.licenseutilizationcalculator.pojo.CapacityUnit;
import co.brtel.licenseutilizationcalculator.pojo.FeatureInformation;
import co.brtel.licenseutilizationcalculator.pojo.FeatureState;
import co.brtel.licenseutilizationcalculator.pojo.ManagedObject;
import co.brtel.licenseutilizationcalculator.pojo.Parameter;

public class FeatureCodeInformationUtilizationCalculator {
	private Map<String, List<ManagedObject>> managedObjectsMap;
	private static final String ENABLED = "Enabled";
	private static final String SUPPORTED = "Supported";
	private static final String WCEL = "WCEL";
	private static final String WBTS = "WBTS";

	public FeatureCodeInformationUtilizationCalculator(List<ManagedObject> managedObjects, Set<String> rncNames) {
		managedObjectsMap = new HashMap<String, List<ManagedObject>>();
		for (String rncName : rncNames) {
			List<ManagedObject> mos = managedObjects.stream().filter(item -> item.getDistName().contains("RNC-" + rncName.replace("R", "").replace("N", "")))
					.collect(Collectors.toList());
			managedObjectsMap.put(rncName, mos);
		}
	}

	public void calculateUtilization(List<FeatureInformation> featureInformations) {
		for (FeatureInformation featureInformation : featureInformations.stream()
				.filter(item -> item.getFeatureState() == FeatureState.ON && item.getCapacityUnit() != CapacityUnit.DYNAMIC_CELL && item.getCapacityUnit() != CapacityUnit.ON_OFF)
				.collect(Collectors.toList())) {
			calculate(featureInformation);
		}
	}

	private void calculate(FeatureInformation featureInformation) {
		switch (featureInformation.getManagedObjectType()) {
		case RNFC:
			calculateUtilizationBasedOnRnfc(featureInformation);
			break;
		case WCEL:
			calculateUtilizationBasedOnWcell(featureInformation);
			break;
		default:
			break;
		}
	}

	private String getParameterValue(ManagedObject managedObject, String name) {
		Optional<Parameter> hSUPAEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase(name)).findAny();
		if(hSUPAEnabled.isPresent())
			return hSUPAEnabled.get().getValue();
		return "";
	}
	
	private void calculateUtilizationBasedOnWcell(FeatureInformation featureInformation) {
		long count = 0;
		List<ManagedObject> wcells = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL))
				.collect(Collectors.toList());
		if (featureInformation.getCode().equals("638") || featureInformation.getCode().equals("966")) {
			count = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count();
		} else if (featureInformation.getCode().equals("961") || featureInformation.getCode().equals("962")) {
			Map<String, Boolean> tmpMap = new HashMap<>();
			for (ManagedObject wcell : wcells) {
				String hSUPAEnabled = getParameterValue(wcell, "HSUPAEnabled");
				if (hSUPAEnabled.equalsIgnoreCase(ENABLED))
					tmpMap.put(Arrays.stream(wcell.getDistName().split("/")).filter(item -> item.startsWith(WBTS)).findAny().get(), true);
			}
			count = tmpMap.size();
		} else if (featureInformation.getCode().equals("1086")) {
			for (ManagedObject wcell : wcells) {
				String hSUPAEnabled = getParameterValue(wcell, "HSUPA2MSTTIEnabled");
				String maxTotalUplinkSymbolRate = getParameterValue(wcell, "MaxTotalUplinkSymbolRate");
				if (hSUPAEnabled.equalsIgnoreCase(ENABLED) && maxTotalUplinkSymbolRate.equalsIgnoreCase("3"))
					count++;
			}
		} else if (featureInformation.getCode().equals("1087")) {

			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell, "HSDPA64UsersEnabled").equalsIgnoreCase(ENABLED))
					count++;
			}
		} else if (featureInformation.getCode().equals("1089")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HSPAQoSEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1091")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HspaMultiNrtRabSupport").equalsIgnoreCase(SUPPORTED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1471")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HSDPA64QAMallowed").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1475")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HSUPA2MSTTIEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1476")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"FDPCHEnabled").equalsIgnoreCase(ENABLED) && getParameterValue(wcell,"CPCEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1485")) {
			for (ManagedObject wcell : wcells) {
				// TODO: 1 cell found!
				if (getParameterValue(wcell,"PCH24kbpsEnabled").equalsIgnoreCase(ENABLED) && new Integer(getParameterValue(wcell,"NbrOfSCCPCHs")) > 1) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1497")) {
			for (ManagedObject wcell : wcells) {
				// TODO: 1 cell found!
				if (getParameterValue(wcell,"HSUPA16QAMAllowed").equalsIgnoreCase(ENABLED) && getParameterValue(wcell,"MaxTotalUplinkSymbolRate").equalsIgnoreCase("3")) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1755")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"LTECellReselection").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1756")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"IncomingLTEISHO").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1795")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HSFACHEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("1798")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"MDTPeriodicMeasEnabled").equalsIgnoreCase("1")) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3414")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"LTEHandoverEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3420")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"SABEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3422")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"AppAwareRANEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3898")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"LayeringRRCRelEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("3903")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"SmartLTELayeringEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4160")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"FastHSPAMobilityEnabled").toLowerCase().contains(ENABLED.toLowerCase())) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4492")) {
			for (ManagedObject wcell : wcells) {
				if (new Integer(getParameterValue(wcell,"MassEventHandler")) > 0) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4533")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"HSFACHDRXEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4538")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"CPCEnabled").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4545")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"VoiceCallPriority").equalsIgnoreCase(ENABLED)) {
					count++;
				}
			}
		} else if (featureInformation.getCode().equals("4839")) {
			for (ManagedObject wcell : wcells) {
				if (getParameterValue(wcell,"SmartLTELayeringEnabled").toLowerCase().contains(ENABLED.toLowerCase()))
					count++;
			}
		}
		featureInformation.setUtilization(new Long(count).toString());
		
		System.out.println("RNC-" + featureInformation.getRnc().getName() + ", FeatureCode-" + featureInformation.getCode() + ", Utilization="
				+ featureInformation.getUtilization());
	}

	private void calculateUtilizationBasedOnRnfc(FeatureInformation featureInformation) {
		Optional<ManagedObject> opt = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("RNFC")).findAny();
		if (!opt.isPresent())
			return;
		ManagedObject managedObject = opt.get();

		if (featureInformation.getCode().equals("630")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("AMRWithEDCH")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("885")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("EmergencyCallRedirect")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("967")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("968")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("AMRWithHSDSCH")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("975")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("SubscriberTrace")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("1060")) {
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("IFHOOverIurEnabled")).findAny();
			if (parameter.isPresent()) {
				if (parameter.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("1061")) {
			Optional<Parameter> hsdpaMobility = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			Optional<Parameter> hspaInterRNCMobility = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("HSPAInterRNCMobility"))
					.findAny();
			if (hsdpaMobility.isPresent() && hspaInterRNCMobility.isPresent()) {
				if (hsdpaMobility.get().getValue().equalsIgnoreCase(ENABLED) && hspaInterRNCMobility.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("1062")) {
			Optional<Parameter> wireLessPriorityService = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("WireLessPriorityService"))
					.findAny();
			Optional<Parameter> wpsCallRestriction = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("WPSCallRestriction")).findAny();
			if (wireLessPriorityService.isPresent() && wpsCallRestriction.isPresent()) {
				if (wireLessPriorityService.get().getValue().equalsIgnoreCase(ENABLED) && wpsCallRestriction.get().getValue().equalsIgnoreCase(ENABLED))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("1686")) {
			Optional<Parameter> hsdpaMobility = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			Optional<Parameter> frlcEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("FRLCEnabled")).findAny();
			if (hsdpaMobility.isPresent() && frlcEnabled.isPresent()) {
				if (hsdpaMobility.get().getValue().equalsIgnoreCase(ENABLED) && frlcEnabled.get().getValue().equalsIgnoreCase(ENABLED)) {
					Stream<ManagedObject> wcels = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL));
					int count = 0;
					for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
						Optional<Parameter> dCellHSDPAEnabled = Arrays.stream(wcell.getParameters()).filter(item -> item.getName().equalsIgnoreCase("DCellHSDPAEnabled")).findAny();
						if (dCellHSDPAEnabled.isPresent() && dCellHSDPAEnabled.get().getValue().equalsIgnoreCase(ENABLED))
							count++;
					}
					featureInformation.setUtilization(new Long(count).toString());
				}
			}
		} else if (featureInformation.getCode().equals("1690")) {
			Optional<Parameter> rrcSetupCCHEnabledR99 = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("RRCSetupCCHEnabledR99"))
					.findAny();
			if (rrcSetupCCHEnabledR99.isPresent() && rrcSetupCCHEnabledR99.get().getValue().equalsIgnoreCase(ENABLED)) {
				Stream<ManagedObject> wcels = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase(WCEL));
				int count = 0;
				for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
					Optional<Parameter> ccHSetupEnabled = Arrays.stream(wcell.getParameters()).filter(item -> item.getName().equalsIgnoreCase("CCHSetupEnabled")).findAny();
					if (ccHSetupEnabled.isPresent() && ccHSetupEnabled.get().getValue().equalsIgnoreCase(ENABLED))
						count++;
				}
				featureInformation.setUtilization(new Long(count).toString());
			}
		} else if (featureInformation.getCode().equals("3384")) {
			Optional<Parameter> lfdProfEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("LFDProfEnabled")).findAny();
			if (lfdProfEnabled.isPresent() && lfdProfEnabled.get().getValue().equalsIgnoreCase(ENABLED)) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("4290")) {
			Optional<Parameter> autoACResEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("AutoACResEnabled")).findAny();
			if (autoACResEnabled.isPresent() && new Integer(autoACResEnabled.get().getValue()) > 0) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("4293")) {
			Optional<Parameter> fastPCHSwitchEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("FastPCHSwitchEnabled"))
					.findAny();
			if (fastPCHSwitchEnabled.isPresent() && fastPCHSwitchEnabled.get().getValue().equalsIgnoreCase(ENABLED)) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("4348")) {
			Optional<Parameter> dch00SuppOnIurEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("DCH00SuppOnIurEnabled"))
					.findAny();
			if (dch00SuppOnIurEnabled.isPresent() && dch00SuppOnIurEnabled.get().getValue().equalsIgnoreCase(ENABLED)) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		} else if (featureInformation.getCode().equals("4780")) {
			Optional<Parameter> rrcReDirEnabled = Arrays.stream(managedObject.getParameters()).filter(item -> item.getName().equalsIgnoreCase("RRCReDirEnabled")).findAny();
			if (rrcReDirEnabled.isPresent() && rrcReDirEnabled.get().getValue().equalsIgnoreCase(ENABLED)) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase(WCEL)).count()).toString());
			}
		}
		if (featureInformation.getUtilization() == "-1")
			featureInformation.setUtilization("0");
		System.out.println("RNC-" + featureInformation.getRnc().getName() + ", FeatureCode-" + featureInformation.getCode() + ", Utilization="
				+ featureInformation.getUtilization());
	}
}
