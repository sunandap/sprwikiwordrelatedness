/**
 * Provides implementations of the Word-to-Vertex mapping algorithms.
 * <p>
 * Used to create the word-to-vertex maps.
 * <p>
 * Programs are organized in the following pipeline:
 * <ol>
 *   <li>{@link CreateLinkWordMapping} or {@link CreateTitleWordMapping} -- Create a list of words-to-vertex mappings.</li>
 *   <li>{@link CreateMappings} -- Given the list, make it searchable and usable in a program.</li>
 * </ol>
 * 
 * @author weale
 */
package edu.osu.slate.relatedness.swwr.setup.wordmapping;
