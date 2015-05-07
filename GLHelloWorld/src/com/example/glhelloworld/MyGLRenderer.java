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

	public MyGLRenderer(Context context) {
		m_Context = context;
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
		float GridStartZValue = -15; // -20;
		float GridStartXValue = -15;
		float GridSpacing = 1.0f;

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
		}

		// Draw Objects
		m_Cube.DrawObject(m_Camera, m_PointLight);
		m_Cube2.DrawObject(m_Camera, m_PointLight);

		// Update and Draw Grid
		UpdateGravityGrid();
		m_Grid.DrawGrid(m_Camera);
	}
}
