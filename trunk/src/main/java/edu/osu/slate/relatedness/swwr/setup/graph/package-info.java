/**
 * Programs for creating the wiki graph.
 * <p>
 * The overall pipeline is as follows:
 * <ol>
 *   <li>{@link CreateIDToVertexFile}</li>
 *   <li>{@link CreateTitleIDFiles}</li>
 *   <li>{@link CreateRedirectFiles}</li>
 *   <li>{@link CreateGraphFiles}</li>
 * </ol>
 * @author weale
 */
package edu.osu.slate.relatedness.swwr.setup.graph;
