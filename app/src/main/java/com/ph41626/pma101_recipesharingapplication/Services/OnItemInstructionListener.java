package com.ph41626.pma101_recipesharingapplication.Services;

import com.ph41626.pma101_recipesharingapplication.Model.Instruction;
import com.ph41626.pma101_recipesharingapplication.Model.Media;

import java.util.ArrayList;

public interface OnItemInstructionListener {
    void removeItemInstruction(int position,Instruction instruction);
    void removeItemMedia(String mediaId,Instruction instruction,int pos);
    void chooseImage(Instruction instruction,int pos);
    ArrayList<Media> getMedias();
}
