/*
 * Copyright 2023 CodeThink Technologies and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.codethink.xiaoming.cause;

import com.google.common.base.Preconditions;

public class DescriptionCause
    extends AbstractCause {

    private final String description;

    public DescriptionCause(String description) {
        Preconditions.checkNotNull(description, "Description is null!");
        Preconditions.checkArgument(!description.isEmpty(), "Description is empty!");

        this.description = description;
    }

    public DescriptionCause(String description, Cause cause) {
        super(cause);

        Preconditions.checkNotNull(description, "Description is null!");
        Preconditions.checkArgument(!description.isEmpty(), "Description is empty!");

        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DescriptionCause)) {
            return false;
        }
        final DescriptionCause descriptionCause = (DescriptionCause) obj;
        return description.equals(descriptionCause.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }

    @Override
    public String toString() {
        return description;
    }
}
