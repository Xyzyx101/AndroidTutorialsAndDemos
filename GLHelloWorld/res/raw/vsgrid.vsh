uniform mat4 uMVPMatrix;
attribute vec3 aPosition;

//uniform float time;

// Gravity Grid
uniform int NumberMasses; 

const int MAX_MASSES = 30;
uniform float MassValues[MAX_MASSES];  
uniform vec3  MassLocations[MAX_MASSES]; 
uniform float MassEffectiveRadius[MAX_MASSES];
uniform vec3 SpotLightColor[MAX_MASSES];

uniform vec3 vColor;
varying vec3 Color;


// returns Intensity from 0 to 1 
// most intense in the center of circle and 0 when
// Radius = MaxRadius 
float IntensityCircle(float Radius, float MaxRadius)
{
    float retval;	
    retval = 1.0 - (Radius/MaxRadius);
	return retval;
}

void main()
{
	//float height; 
	//float ZValue;
	
	vec3  NewPos;
	
	//height = sin(aPosition.x+time) + aPosition.y;
	//ZValue = sin(aPosition.x+time) + aPosition.z;
	
	//NewPos = vec3(aPosition.x, height, ZValue);
	NewPos = aPosition;

	// F = G *( M1 * M2)/ (R*R)
	// F = m * a
	// F/m = a
	// Force = (MassOnGravityGrid * MassVertex) / (RadiusBetweenMasses * RadiusBetweenMasses);
	float Force;
	float ForceMax = 0.6; //0.5;
	vec3 VertexPos = NewPos;
	
	vec3 MassSpotLightColor = vec3(0,0,0);
	
	for (int i = 0; i < MAX_MASSES; i++)
	{
		// If mass value is valid then process this mass for the grid
		if (MassValues[i] > 0.0) {
			vec3 Mass2Vertex = VertexPos - MassLocations[i];
			vec3 DirectionToVertex = normalize(Mass2Vertex);
			vec3 DirectionToMass = -DirectionToVertex;
		
			float R = length(Mass2Vertex);
		
			Force =  (MassValues[i] * (2.0)) / (R * R); 
	
			if (R < MassEffectiveRadius[i])	{
				float Intensity = IntensityCircle(R, MassEffectiveRadius[i]);
				MassSpotLightColor = MassSpotLightColor + (SpotLightColor[i] * Intensity);
			}
	
			Force = min(Force, ForceMax);
		
			//VertexPos = VertexPos + (Force * DirectionToVertex);
			VertexPos = VertexPos + (Force * DirectionToMass);
		}
	}
	
    gl_Position = uMVPMatrix * vec4(VertexPos,1);
    Color = vColor + MassSpotLightColor;
} 