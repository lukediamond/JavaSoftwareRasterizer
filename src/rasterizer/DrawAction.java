/*
 * Luke Diamond
 * Mr. Patterson
 * Grade 11 Final Project
 * 01/22/2018
 */

package rasterizer;

class DrawAction {
	public int tex;
	public Matrix4 model;
	public Matrix4 view;
	public Matrix4 proj;
	public Vector3 va;
	public Vector3 vb;
	public Vector3 vc;
	public Vector2 ta;
	public Vector2 tb;
	public Vector2 tc;

	public DrawAction(
		int tex_, 
		Matrix4 model_, 
		Matrix4 view_,
		Matrix4 proj_, 
		Vector3 va_, 
		Vector3 vb_, 
		Vector3 vc_, 
		Vector2 ta_, 
		Vector2 tb_, 
		Vector2 tc_) {
		tex = tex_;
		model = model_;
		view = view_;
		proj = proj_;
		va = va_;
		vb = vb_;
		vc = vc_;
		ta = ta_;
		tb = tb_;
		tc = tc_;
	}
}