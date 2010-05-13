/**
 * Generates non-uniform transitions based on the page word text.
 * <p>
 * The overall pipeline is as follows:
 * <ol>
 *   <li>{@link TrimToVertices} -- Remove unneeded pages from the xml file.
 *   <li>{@link CompareTextVectors} -- Perform the cosine relatedness metric on pages.
 * </ol>
 * 
 * @author weale
 */
package edu.osu.slate.relatedness.swwr.setup.textcompare;
