package net.jfabricationgames.gdx.cutscene.action;

import net.jfabricationgames.gdx.sound.SoundManager;
import net.jfabricationgames.gdx.sound.SoundSet;

public class CutsceneSoundEffectAction extends AbstractCutsceneAction {
	
	private static final SoundSet soundSet = SoundManager.getInstance().loadSoundSet("cutscene");
	
	public CutsceneSoundEffectAction(CutsceneUnitProvider unitProvider, CutsceneControlledActionConfig actionConfig) {
		super(unitProvider, actionConfig);
	}
	
	@Override
	public void execute(float delta) {
		soundSet.playSound(actionConfig.soundEffectName);
	}
	
	@Override
	public boolean isExecutionFinished() {
		return true;
	}
}
