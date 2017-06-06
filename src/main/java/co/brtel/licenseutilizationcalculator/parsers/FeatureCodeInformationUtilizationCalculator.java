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
	
	public FeatureCodeInformationUtilizationCalculator(List<ManagedObject> managedObjects, Set<String> rncNames){
		managedObjectsMap = new HashMap<String, List<ManagedObject>>();
		for (String rncName : rncNames) {
			List<ManagedObject> mos = managedObjects.stream().filter(item ->item.getDistName().contains("RNC-" + rncName.replace("R", "").replace("N", ""))).collect(Collectors.toList());
			managedObjectsMap.put(rncName, mos);
		}
	}
	
	public void calculateUtilization(List<FeatureInformation> featureInformations){
		for (FeatureInformation featureInformation : 
			featureInformations.stream().filter(item -> item.getFeatureState() == FeatureState.ON 
			&& item.getCapacityUnit() != CapacityUnit.DYNAMIC_CELL 
			&& item.getCapacityUnit() != CapacityUnit.ON_OFF).collect(Collectors.toList())) {
			calculate(featureInformation);
		}
	}

	private void calculate(FeatureInformation featureInformation) {
		switch (featureInformation.getManagedObjectType()) {
		case RNFC:
			calculateUtilizationBasedOnRnfc(featureInformation);
			break;
		default:
			break;
		}
	}

	private void calculateUtilizationBasedOnRnfc(FeatureInformation featureInformation) {
		Optional<ManagedObject> opt = managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("RNFC")).findAny();
		if (!opt.isPresent())
			return;
		ManagedObject managedObject = opt.get();
		
		if(featureInformation.getCode().equals("630")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("AMRWithEDCH")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("885")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("EmergencyCallRedirect")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("967")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("968")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("AMRWithHSDPA")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("975")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("SubscriberTrace")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("1060")){
			Optional<Parameter> parameter = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("IFHOOverIurEnabled")).findAny();
			if(parameter.isPresent()){
				if(parameter.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("1061")){
			Optional<Parameter> hsdpaMobility = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			Optional<Parameter> hspaInterRNCMobility = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("HSPAInterRNCMobility")).findAny();
			if(hsdpaMobility.isPresent() && hspaInterRNCMobility.isPresent()){
				if(hsdpaMobility.get().getValue().equalsIgnoreCase("Enabled") && hspaInterRNCMobility.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("1062")){
			Optional<Parameter> wireLessPriorityService = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("WireLessPriorityService")).findAny();
			Optional<Parameter> wpsCallRestriction = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("WPSCallRestriction")).findAny();
			if(wireLessPriorityService.isPresent() && wpsCallRestriction.isPresent()){
				if(wireLessPriorityService.get().getValue().equalsIgnoreCase("Enabled") && wpsCallRestriction.get().getValue().equalsIgnoreCase("Enabled"))
					featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream().filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("1686")){
			Optional<Parameter> hsdpaMobility = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("HSDPAMobility")).findAny();
			Optional<Parameter> frlcEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("FRLCEnabled")).findAny();
			if(hsdpaMobility.isPresent() && frlcEnabled.isPresent()){
				if(hsdpaMobility.get().getValue().equalsIgnoreCase("Enabled") && frlcEnabled.get().getValue().equalsIgnoreCase("Enabled")){
					Stream<ManagedObject> wcels = managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
							.filter(item -> item.getClassName().equalsIgnoreCase("WCEL"));
					int count = 0;
					for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
						Optional<Parameter> dCellHSDPAEnabled = Arrays.stream(wcell.getParameters()).filter(item->item.getName().equalsIgnoreCase("DCellHSDPAEnabled")).findAny();
						if(dCellHSDPAEnabled.isPresent() && dCellHSDPAEnabled.get().getValue().equalsIgnoreCase("Enabled"))
							count++;
					}
					featureInformation.setUtilization(new Long(count).toString());
				}
			}
		}else if(featureInformation.getCode().equals("1690")){
			Optional<Parameter> rrcSetupCCHEnabledR99 = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("RRCSetupCCHEnabledR99")).findAny();
			if(rrcSetupCCHEnabledR99.isPresent() && rrcSetupCCHEnabledR99.get().getValue().equalsIgnoreCase("Enabled")){
				Stream<ManagedObject> wcels = managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL"));
				int count = 0;
				for (ManagedObject wcell : wcels.collect(Collectors.toList())) {
					Optional<Parameter> ccHSetupEnabled = Arrays.stream(wcell.getParameters()).filter(item->item.getName().equalsIgnoreCase("CCHSetupEnabled")).findAny();
					if(ccHSetupEnabled.isPresent() && ccHSetupEnabled.get().getValue().equalsIgnoreCase("Enabled"))
						count++;
				}
				featureInformation.setUtilization(new Long(count).toString());
			}
		}else if(featureInformation.getCode().equals("3384")){
			Optional<Parameter> lfdProfEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("LFDProfEnabled")).findAny();
			if (lfdProfEnabled.isPresent() && lfdProfEnabled.get().getValue().equalsIgnoreCase("Enabled")) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("4290")){
			Optional<Parameter> autoACResEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("AutoACResEnabled")).findAny();
			if (autoACResEnabled.isPresent() && new Integer(autoACResEnabled.get().getValue()) > 0) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("4293")){
			Optional<Parameter> fastPCHSwitchEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("FastPCHSwitchEnabled")).findAny();
			if (fastPCHSwitchEnabled.isPresent() && fastPCHSwitchEnabled.get().getValue().equalsIgnoreCase("Enabled")) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("4348")){
			Optional<Parameter> dch00SuppOnIurEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("DCH00SuppOnIurEnabled")).findAny();
			if (dch00SuppOnIurEnabled.isPresent() && dch00SuppOnIurEnabled.get().getValue().equalsIgnoreCase("Enabled")) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}else if(featureInformation.getCode().equals("4780")){
			Optional<Parameter> rrcReDirEnabled = Arrays.stream(managedObject.getParameters()).filter(item->item.getName().equalsIgnoreCase("RRCReDirEnabled")).findAny();
			if (rrcReDirEnabled.isPresent() && rrcReDirEnabled.get().getValue().equalsIgnoreCase("Enabled")) {
				featureInformation.setUtilization(new Long(managedObjectsMap.get(featureInformation.getRnc().getName()).stream()
						.filter(item -> item.getClassName().equalsIgnoreCase("WCEL")).count()).toString());
			}
		}
		if(featureInformation.getUtilization() == null)
			featureInformation.setUtilization("0");
		System.out.println("RNC-" + featureInformation.getRnc().getName() + ", FeatureCode-" + featureInformation.getCode() + ", Utilization=" + featureInformation.getUtilization());
	}
}
