/* Copyright 2010 Speech and Language Technologies Lab, The Ohio State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.osu.slate.relatedness.swwr.data;

import java.util.*;

/**
 * Comparator for the {@link TitleID} class.
 * <p>
 * Comparisons are done based on the ids of each object.<br>
 * (int comparison) 
 * 
 * @author weale
 *
 */
public class TIDIDComparator implements Comparator<TitleID>
{
	public int compare(TitleID tid1, TitleID tid2)
	{
		return tid1.getID() - tid2.getID();
	}
}
