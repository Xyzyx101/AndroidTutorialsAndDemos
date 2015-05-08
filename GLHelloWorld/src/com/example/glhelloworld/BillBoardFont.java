package com.example.glhelloworld;


import android.content.Context;

public class BillBoardFont extends BillBoard {
	private char m_Character;

	BillBoardFont(
			Context iContext,
			Mesh iMesh,
			MeshEx iMeshEx,
			Texture[] iTextures,
			Material iMaterial,
			Shader iShader,

			char Character) {
		super(iContext, iMesh, iMeshEx, iTextures, iMaterial, iShader);

		m_Character = Character;
	}

	char GetCharacter() {
		return m_Character;
	}

	void SetCharacter(char value) {
		m_Character = value;
	}

	boolean IsFontCharacter(char value) {
		if (m_Character == value) {
			return true;
		} else {
			return false;
		}
	}
}
