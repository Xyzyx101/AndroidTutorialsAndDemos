package com.example.glhelloworld;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.content.Context;

import android.util.Log;

import android.media.AudioManager;
import android.media.SoundPool;

public class MyGLRenderer implements GLSurfaceView.Renderer {
	private Context m_Context;

	private PointLight m_PointLight;
	private Camera m_Camera;

	private int m_ViewPortWidth;
	private int m_ViewPortHeight;

	private Cube m_Cube;
	private Cube m_Cube2;

	private Vector3 m_CubePositionDelta = new Vector3(0.05f, 0.05f, 0.05f);
	private Vector3 m_CubeRotationAxis2 = new Vector3(0, 1, 0);
	private Vector3 m_CubeScale = new Vector3(1, 1, 1.0f);

	// Gravity
	private Vector3 m_Force1 = new Vector3(0, 20, 0);
	private float m_RotationalForce = 3;

	// Gravity Grid
	private GravityGridEx m_Grid;

	// SFX
	private SoundPool m_SoundPool;
	private int m_SoundIndex1;
	private int m_SoundIndex2;
	private boolean m_SFXOn = true;

	// HUD
	// Character Set
	private Texture[] m_CharacterSetTextures = new Texture[BillBoardCharacterSet.MAX_CHARACTERS];
	private BillBoardCharacterSet m_CharacterSet = null;

	// HUD
	private Texture m_HUDTexture = null;
	private BillBoard m_HUDComposite = null;
	private HUD m_HUD = null;

	private int m_Health = 100;
	private int m_Score = 0;

	public MyGLRenderer(Context context) {
		m_Context = context;
	}

	// HUD
	void CreateCharacterSetTextures(Context iContext) {
		// Numeric
		m_CharacterSetTextures[0] = new Texture(iContext, R.drawable.charset1);
		m_CharacterSetTextures[1] = new Texture(iContext, R.drawable.charset2);
		m_CharacterSetTextures[2] = new Texture(iContext, R.drawable.charset3);
		m_CharacterSetTextures[3] = new Texture(iContext, R.drawable.charset4);
		m_CharacterSetTextures[4] = new Texture(iContext, R.drawable.charset5);
		m_CharacterSetTextures[5] = new Texture(iContext, R.drawable.charset6);
		m_CharacterSetTextures[6] = new Texture(iContext, R.drawable.charset7);
		m_CharacterSetTextures[7] = new Texture(iContext, R.drawable.charset8);
		m_CharacterSetTextures[8] = new Texture(iContext, R.drawable.charset9);
		m_CharacterSetTextures[9] = new Texture(iContext, R.drawable.charset0);

		// Alphabet
		m_CharacterSetTextures[10] = new Texture(iContext, R.drawable.charseta);
		m_CharacterSetTextures[11] = new Texture(iContext, R.drawable.charsetb);
		m_CharacterSetTextures[12] = new Texture(iContext, R.drawable.charsetc);
		m_CharacterSetTextures[13] = new Texture(iContext, R.drawable.charsetd);
		m_CharacterSetTextures[14] = new Texture(iContext, R.drawable.charsete);
		m_CharacterSetTextures[15] = new Texture(iContext, R.drawable.charsetf);
		m_CharacterSetTextures[16] = new Texture(iContext, R.drawable.charsetg);
		m_CharacterSetTextures[17] = new Texture(iContext, R.drawable.charseth);
		m_CharacterSetTextures[18] = new Texture(iContext, R.drawable.charseti);
		m_CharacterSetTextures[19] = new Texture(iContext, R.drawable.charsetj);
		m_CharacterSetTextures[20] = new Texture(iContext, R.drawable.charsetk);
		m_CharacterSetTextures[21] = new Texture(iContext, R.drawable.charsetl);
		m_CharacterSetTextures[22] = new Texture(iContext, R.drawable.charsetm);
		m_CharacterSetTextures[23] = new Texture(iContext, R.drawable.charsetn);
		m_CharacterSetTextures[24] = new Texture(iContext, R.drawable.charseto);
		m_CharacterSetTextures[25] = new Texture(iContext, R.drawable.charsetp);
		m_CharacterSetTextures[26] = new Texture(iContext, R.drawable.charsetq);
		m_CharacterSetTextures[27] = new Texture(iContext, R.drawable.charsetr);
		m_CharacterSetTextures[28] = new Texture(iContext, R.drawable.charsets);
		m_CharacterSetTextures[29] = new Texture(iContext, R.drawable.charsett);
		m_CharacterSetTextures[30] = new Texture(iContext, R.drawable.charsetu);
		m_CharacterSetTextures[31] = new Texture(iContext, R.drawable.charsetv);
		m_CharacterSetTextures[32] = new Texture(iContext, R.drawable.charsetw);
		m_CharacterSetTextures[33] = new Texture(iContext, R.drawable.charsetx);
		m_CharacterSetTextures[34] = new Texture(iContext, R.drawable.charsety);
		m_CharacterSetTextures[35] = new Texture(iContext, R.drawable.charsetz);

		// Debug Symbols
		m_CharacterSetTextures[36] = new Texture(iContext,
				R.drawable.charsetcolon);
		m_CharacterSetTextures[37] = new Texture(iContext,
				R.drawable.charsetsemicolon);
		m_CharacterSetTextures[38] = new Texture(iContext,
				R.drawable.charsetcomma);
		m_CharacterSetTextures[39] = new Texture(iContext,
				R.drawable.charsetequals);
		m_CharacterSetTextures[40] = new Texture(iContext,
				R.drawable.charsetleftparen);
		m_CharacterSetTextures[41] = new Texture(iContext,
				R.drawable.charsetrightparen);

		m_CharacterSetTextures[42] = new Texture(iContext,
				R.drawable.charsetdot);
	}

	void SetUpHUDComposite(Context iContext) {
		m_HUDTexture = new Texture(iContext, R.drawable.hud);

		// Set up HUD Composite BillBoard
		// Create Shader
		// Shader Shader = m_OneLightTexShader; //new Shader(iContext,
		// R.raw.vsonelight, R.raw.fsonelight); // ok
		Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight); // ok
		// Shader Shader = new Shader(m_Context, R.raw.vsonelight,
		// R.raw.fsonelightnodiffuse); // ok

		// Create Debug Local Axis Shader
		// Shader LocalAxisShader = m_LocalAxisShader; // new Shader(iContext,
		// R.raw.vslocalaxis, R.raw.fslocalaxis);

		// MeshEx(int CoordsPerVertex,
		// int MeshVerticesDataPosOffset,
		// int MeshVerticesUVOffset ,
		// int MeshVerticesNormalOffset,
		// float[] Vertices,
		// short[] DrawOrder
		MeshEx Mesh = new MeshEx(8, 0, 3, 5, Cube.CubeData, Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		Material1.SetEmissive(1.0f, 1.0f, 1.0f);
		// Material1.SetAlpha(0.5f);

		Texture[] Tex = new Texture[1];
		Tex[0] = m_HUDTexture;
		// CubeTex[1] = m_TexGreenAndroid;

		m_HUDComposite = new BillBoard(iContext,
		// null,
				Mesh, Tex, Material1, Shader// ,
		// LocalAxisShader
		);

		// Set Intial Position and Orientation
		// Vector3 Axis = new Vector3(1,1,1);
		Vector3 Position = new Vector3(0.0f, 3.0f, 0.0f);
		Vector3 Scale = new Vector3(1.0f, 0.1f, 0.01f);

		m_HUDComposite.m_Orientation.SetPosition(Position);
		// m_HUDComposite.m_Orientation.SetRotationAxis(Axis);
		m_HUDComposite.m_Orientation.SetScale(Scale);

		m_HUDComposite.GetObjectPhysics().SetGravity(false);

		// Set black portion of HUD to transparent
		m_HUDComposite.GetMaterial().SetAlpha(1.0f);
		m_HUDComposite.SetBlend(true);
	}

	void CreateCharacterSet(Context iContext) {
		// Create Shader
		// Shader Shader = new Shader(iContext, R.raw.vsonelight,
		// R.raw.fsonelight); // ok
		Shader Shader = new Shader(iContext, R.raw.vsonelight,
				R.raw.fsonelightnodiffuse); // ok

		// Create Debug Local Axis Shader
		// Shader LocalAxisShader = m_LocalAxisShader; //new Shader(iContext,
		// R.raw.vslocalaxis, R.raw.fslocalaxis);

		// MeshEx(int CoordsPerVertex,
		// int MeshVerticesDataPosOffset,
		// int MeshVerticesUVOffset ,
		// int MeshVerticesNormalOffset,
		// float[] Vertices,
		// short[] DrawOrder
		MeshEx Mesh = new MeshEx(8, 0, 3, 5, Cube.CubeData, Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		Material1.SetEmissive(1.0f, 1.0f, 1.0f);
		// Material1.SetAlpha(0.5f);

		// Create Texture
		CreateCharacterSetTextures(iContext);

		// Setup HUD
		SetUpHUDComposite(iContext);

		m_CharacterSet = new BillBoardCharacterSet();

		int NumberCharacters = 43;
		char[] Characters = new char[BillBoardCharacterSet.MAX_CHARACTERS];
		Characters[0] = '1';
		Characters[1] = '2';
		Characters[2] = '3';
		Characters[3] = '4';
		Characters[4] = '5';
		Characters[5] = '6';
		Characters[6] = '7';
		Characters[7] = '8';
		Characters[8] = '9';
		Characters[9] = '0';

		// AlphaBets
		Characters[10] = 'a';
		Characters[11] = 'b';
		Characters[12] = 'c';
		Characters[13] = 'd';
		Characters[14] = 'e';
		Characters[15] = 'f';
		Characters[16] = 'g';
		Characters[17] = 'h';
		Characters[18] = 'i';
		Characters[19] = 'j';
		Characters[20] = 'k';
		Characters[21] = 'l';
		Characters[22] = 'm';
		Characters[23] = 'n';
		Characters[24] = 'o';
		Characters[25] = 'p';
		Characters[26] = 'q';
		Characters[27] = 'r';
		Characters[28] = 's';
		Characters[29] = 't';
		Characters[30] = 'u';
		Characters[31] = 'v';
		Characters[32] = 'w';
		Characters[33] = 'x';
		Characters[34] = 'y';
		Characters[35] = 'z';

		// Debug
		Characters[36] = ':';
		Characters[37] = ';';
		Characters[38] = ',';
		Characters[39] = '=';
		Characters[40] = '(';
		Characters[41] = ')';
		Characters[42] = '.';

		for (int i = 0; i < NumberCharacters; i++) {
			Texture[] Tex = new Texture[1];
			Tex[0] = m_CharacterSetTextures[i];

			BillBoardFont Font = new BillBoardFont(iContext,
			// null,
					Mesh, Tex, Material1, Shader,
					// LocalAxisShader,
					Characters[i]);

			Font.GetObjectPhysics().SetGravity(false);
			m_CharacterSet.AddToCharacterSet(Font);
		}

	}

	void CreateHealthItem() {
		Texture HUDTexture = new Texture(m_Context, R.drawable.hud);

		// Set up HUD Composite BillBoard
		// Create Shader
		Shader Shader = new Shader(m_Context, R.raw.vsonelight,
				R.raw.fsonelightnodiffuse); // ok

		// Create Debug Local Axis Shader
		// Shader LocalAxisShader = m_LocalAxisShader; //new Shader(m_Context,
		// R.raw.vslocalaxis, R.raw.fslocalaxis);

		// MeshEx(int CoordsPerVertex,
		// int MeshVerticesDataPosOffset,
		// int MeshVerticesUVOffset ,
		// int MeshVerticesNormalOffset,
		// float[] Vertices,
		// short[] DrawOrder
		MeshEx Mesh = new MeshEx(8, 0, 3, 5, Cube.CubeData, Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		Material1.SetEmissive(1.0f, 1.0f, 1.0f);
		// Material1.SetAlpha(0.5f);

		Texture[] Tex = new Texture[1];
		Tex[0] = HUDTexture;

		BillBoard HUDHealthComposite = new BillBoard(m_Context,
		// null,
				Mesh, Tex, Material1, Shader// ,
		// LocalAxisShader
		);

		Vector3 Scale = new Vector3(1.0f, 0.1f, 0.01f);
		HUDHealthComposite.m_Orientation.SetScale(Scale);

		HUDHealthComposite.GetObjectPhysics().SetGravity(false);

		// Set Black portion of HUD to transparent
		HUDHealthComposite.GetMaterial().SetAlpha(1.0f);
		HUDHealthComposite.SetBlend(true);

		// Create Health HUD
		Texture HealthTexture = new Texture(m_Context, R.drawable.health);
		Vector3 ScreenPosition = new Vector3(0.8f,
				m_Camera.GetCameraViewportHeight() / 2, 0.5f);

		HUDItem HUDHealth = new HUDItem("health", m_Health, ScreenPosition,
				m_CharacterSet, HealthTexture, HUDHealthComposite);

		if (m_HUD.AddHUDItem(HUDHealth) == false) {
			Log.e("ADDHUDITEM", "CANNOT ADD IN NEW HUD HEALTH ITEM");
		}
	}

	void CreateHUD() {
		// Create HUD
		m_HUD = new HUD(m_Context);

		// Create Score HUD
		Vector3 ScreenPosition = new Vector3(
				-m_Camera.GetCameraViewportWidth() / 2 + 0.4f,
				m_Camera.GetCameraViewportHeight() / 2, 0.5f);

		// Create Score Item for HUD
		HUDItem HUDScore = new HUDItem("score", 0, ScreenPosition,
				m_CharacterSet, null, m_HUDComposite);

		if (m_HUD.AddHUDItem(HUDScore) == false) {
			Log.e("ADDHUDITEM", "CANNOT ADD IN NEW HUD ITEM");
		}

		CreateHealthItem();
		// CreateHealthBonusItem();
	}

	// Update HUD
	void UpdateHUD() {
		m_HUD.UpdateHUDItemNumericalValue("health", m_Health);
		m_HUD.UpdateHUDItemNumericalValue("score", m_Score);
	}

	// SFX
	// Sound Pool
	void CreateSoundPool() {
		/*
		 * 
		 * public SoundPool (int maxStreams, int streamType, int srcQuality)
		 * 
		 * Added in API level 1 Constructor. Constructs a SoundPool object with
		 * the following characteristics:
		 * 
		 * Parameters maxStreams the maximum number of simultaneous streams for
		 * this SoundPool object streamType the audio stream type as described
		 * in AudioManager For example, game applications will normally use
		 * STREAM_MUSIC. srcQuality the sample-rate converter quality. Currently
		 * has no effect. Use 0 for the default.
		 * 
		 * Returns a SoundPool object, or null if creation failed
		 */

		int maxStreams = 10;
		int streamType = AudioManager.STREAM_MUSIC;
		int srcQuality = 0;

		m_SoundPool = new SoundPool(maxStreams, streamType, srcQuality);

		if (m_SoundPool == null) {
			Log.e("RENDERER ",
					"m_SoundPool creation failure!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
	}

	void CreateSound(Context iContext) {
		m_SoundIndex1 = m_Cube.AddSound(m_SoundPool, R.raw.explosion2);
		m_Cube.SetSFXOnOff(m_SFXOn);

		m_SoundIndex2 = m_Cube2.AddSound(m_SoundPool, R.raw.explosion5);
		m_Cube2.SetSFXOnOff(m_SFXOn);
	}

	void CreateGrid(Context iContext) {
		Vector3 GridColor = new Vector3(0.1f, 0.2f, 0.5f);
		float GridHeight = -0.5f;
		float GridStartZValue = -2; // -20;
		float GridStartXValue = -2;
		float GridSpacing = 0.2f;

		// int GridSize = 10;
		int GridSizeZ = 33; // grid vertex points in the z direction
		int GridSizeX = 33; // grid vertex point in the x direction

		// Shader iShader = new Shader(iContext, R.raw.vsonelightnotexture,
		// R.raw.fsonelightnotexture);
		Shader iShader = new Shader(iContext, R.raw.vsgrid, R.raw.fslocalaxis);

		m_Grid = new GravityGridEx(iContext, GridColor, GridHeight,
				GridStartZValue, GridStartXValue, GridSpacing, GridSizeZ,
				GridSizeX, iShader);
	}

	void SetupLights() {
		// Set Light Characteristics
		Vector3 LightPosition = new Vector3(0, 125, 125);

		float[] AmbientColor = new float[3];
		AmbientColor[0] = 0.0f;
		AmbientColor[1] = 0.0f;
		AmbientColor[2] = 0.0f;

		float[] DiffuseColor = new float[3];
		DiffuseColor[0] = 1.0f;
		DiffuseColor[1] = 1.0f;
		DiffuseColor[2] = 1.0f;

		float[] SpecularColor = new float[3];
		SpecularColor[0] = 1.0f;
		SpecularColor[1] = 1.0f;
		SpecularColor[2] = 1.0f;

		m_PointLight.SetPosition(LightPosition);
		m_PointLight.SetAmbientColor(AmbientColor);
		m_PointLight.SetDiffuseColor(DiffuseColor);
		m_PointLight.SetSpecularColor(SpecularColor);
	}

	void SetupCamera() {
		// Set Camera View
		Vector3 Eye = new Vector3(0, 0, 8);
		Vector3 Center = new Vector3(0, 0, -1);
		Vector3 Up = new Vector3(0, 1, 0);

		float ratio = (float) m_ViewPortWidth / m_ViewPortHeight;
		float Projleft = -ratio;
		float Projright = ratio;
		float Projbottom = -1;
		float Projtop = 1;
		float Projnear = 3;
		float Projfar = 50; // 100;

		m_Camera = new Camera(m_Context, Eye, Center, Up, Projleft, Projright,
				Projbottom, Projtop, Projnear, Projfar);
	}

	void CreateCube(Context iContext) {
		// Create Cube Shader
		Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight);

		// MeshEx(int CoordsPerVertex,
		// int MeshVerticesDataPosOffset,
		// int MeshVerticesUVOffset ,
		// int MeshVerticesNormalOffset,
		// float[] Vertices,
		// short[] DrawOrder
		MeshEx CubeMesh = new MeshEx(8, 0, 3, 5, Cube.CubeData4Sided,
				Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		// Material1.SetEmissive(0.0f, 0, 0.25f);

		// Create Texture
		Texture TexAndroid = new Texture(iContext, R.drawable.ic_launcher);

		Texture[] CubeTex = new Texture[1];
		CubeTex[0] = TexAndroid;

		m_Cube = new Cube(iContext, CubeMesh, CubeTex, Material1, Shader);

		// Set Intial Position and Orientation
		Vector3 Axis = new Vector3(0, 1, 0);
		Vector3 Position = new Vector3(0.0f, 2.0f, 0.0f);
		Vector3 Scale = new Vector3(1.0f, 1.0f, 1.0f);

		m_Cube.m_Orientation.SetPosition(Position);
		m_Cube.m_Orientation.SetRotationAxis(Axis);
		m_Cube.m_Orientation.SetScale(Scale);

		// Gravity
		m_Cube.GetObjectPhysics().SetGravity(true);

		// m_Cube.m_Orientation.AddRotation(45);

		// Set Gravity Grid Parameters
		Vector3 GridColor = new Vector3(1, 0, 0);
		m_Cube.SetGridSpotLightColor(GridColor);
		m_Cube.GetObjectPhysics().SetMassEffectiveRadius(6f);
	}

	void CreateCube2(Context iContext) {
		// Create Cube Shader
		Shader Shader = new Shader(iContext, R.raw.vsonelight, R.raw.fsonelight); // ok

		// MeshEx(int CoordsPerVertex,
		// int MeshVerticesDataPosOffset,
		// int MeshVerticesUVOffset ,
		// int MeshVerticesNormalOffset,
		// float[] Vertices,
		// short[] DrawOrder
		// MeshEx CubeMesh = new MeshEx(8,0,3,5,Cube.CubeData,
		// Cube.CubeDrawOrder);
		MeshEx CubeMesh = new MeshEx(8, 0, 3, 5, Cube.CubeData4Sided,
				Cube.CubeDrawOrder);

		// Create Material for this object
		Material Material1 = new Material();
		// Material1.SetEmissive(0.0f, 0, 0.25f);

		// Create Texture
		Texture TexAndroid = new Texture(iContext, R.drawable.ic_launcher);

		Texture[] CubeTex = new Texture[1];
		CubeTex[0] = TexAndroid;

		m_Cube2 = new Cube(iContext, CubeMesh, CubeTex, Material1, Shader);

		// Set Intial Position and Orientation
		Vector3 Axis = new Vector3(0, 1, 0);
		Vector3 Position = new Vector3(0.0f, 4.0f, 0.0f);
		Vector3 Scale = new Vector3(1.0f, 1.0f, 1.0f);

		m_Cube2.m_Orientation.SetPosition(Position);
		m_Cube2.m_Orientation.SetRotationAxis(Axis);
		m_Cube2.m_Orientation.SetScale(Scale);

		// Gravity
		m_Cube2.GetObjectPhysics().SetGravity(true);

		m_Cube.m_Orientation.AddRotation(45);

		// Set Gravity Grid Parameters
		Vector3 GridColor = new Vector3(0, 1, 0);
		m_Cube2.SetGridSpotLightColor(GridColor);
		m_Cube2.GetObjectPhysics().SetMassEffectiveRadius(6f);
	}

	void UpdateGravityGrid() { // Clear Masses from Grid from Previous Update
		// Clear Masses from Grid from Previous Update
		m_Grid.ResetGrid();

		// Add Cubes to Grid
		m_Grid.AddMass(m_Cube);
		m_Grid.AddMass(m_Cube2);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		m_PointLight = new PointLight(m_Context);
		SetupLights();

		// Create a 3d Cube
		CreateCube(m_Context);

		// Create a Second Cube
		CreateCube2(m_Context);

		// Create a new gravity grid
		CreateGrid(m_Context);

		// Create SFX
		CreateSoundPool();
		CreateSound(m_Context);

		// Create HUD
		// Get Width and Height of surface
		m_ViewPortHeight = m_Context.getResources().getDisplayMetrics().heightPixels;
		m_ViewPortWidth = m_Context.getResources().getDisplayMetrics().widthPixels;

		SetupCamera();

		// Create Character Set
		CreateCharacterSet(m_Context);

		CreateHUD();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		// Ignore the passed-in GL10 interface, and use the GLES20
		// class's static methods instead.
		GLES20.glViewport(0, 0, width, height);

		m_ViewPortWidth = width;
		m_ViewPortHeight = height;

		SetupCamera();
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		m_Camera.UpdateCamera();

		// Update Object Physics
		// Cube1
		m_Cube.UpdateObject3d();
		boolean HitGround = m_Cube.GetObjectPhysics().GetHitGroundStatus();
		if (HitGround) {
			// m_Cube.SetVisibility(false);
			m_Cube.GetObjectPhysics().ApplyTranslationalForce(m_Force1);
			m_Cube.GetObjectPhysics().ApplyRotationalForce(m_RotationalForce,
					7.0f);
			m_Cube2.GetObjectPhysics().ApplyRotationalForce(-m_RotationalForce,
					3.0f);
			m_Cube.GetObjectPhysics().ClearHitGroundStatus();
		}

		// Cube2
		m_Cube2.UpdateObject3d();

		// Process Collisions

		Physics.CollisionStatus TypeCollision = m_Cube.GetObjectPhysics()
				.CheckForCollisionSphereBounding(m_Cube, m_Cube2);

		if ((TypeCollision == Physics.CollisionStatus.COLLISION)
				|| (TypeCollision == Physics.CollisionStatus.PENETRATING_COLLISION)) {
			m_Cube.GetObjectPhysics().ApplyLinearImpulse(m_Cube, m_Cube2);

			// SFX
			m_Cube.PlaySound(m_SoundIndex1);
			m_Cube2.PlaySound(m_SoundIndex2);

			// HUD
			m_Health = m_Health - 1;
			if (m_Health < 0) {
				m_Health = 100;
			}
			m_Score = m_Score + 10;
		}

		// Draw Objects
		m_Cube.DrawObject(m_Camera, m_PointLight);
		m_Cube2.DrawObject(m_Camera, m_PointLight);

		// Update and Draw Grid
		UpdateGravityGrid();
		m_Grid.DrawGrid(m_Camera);

		// Update HUD
		UpdateHUD();

		m_HUD.UpdateHUD(m_Camera);

		// Render HUD
		m_HUD.RenderHUD(m_Camera, m_PointLight);

		// Rotate Camera
		// m_Camera.GetOrientation().AddRotation(1);
	}
}
