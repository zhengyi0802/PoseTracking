// Copyright 2020 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.example.posetracking

import android.os.Bundle
import android.util.Log
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.protobuf.InvalidProtocolBufferException

/** Main activity of MediaPipe pose tracking app.  */
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // To show verbose logging, run:
        // adb shell setprop log.tag.MainActivity VERBOSE
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            processor!!.addPacketCallback(
                    OUTPUT_LANDMARKS_STREAM_NAME
            ) { packet: Packet ->
                Log.v(TAG, "Received pose landmarks packet.")
                try {
                    val poseLandmarks = PacketGetter.getProto(packet, NormalizedLandmarkList::class.java)
                    Log.v(
                            TAG,
                            "[TS:"
                                    + packet.timestamp
                                    + "] "
                                    + getPoseLandmarksDebugString(poseLandmarks))
                } catch (exception: InvalidProtocolBufferException) {
                    Log.e(TAG, "Failed to get proto.", exception)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks"
        private fun getPoseLandmarksDebugString(poseLandmarks: NormalizedLandmarkList): String {
            var poseLandmarkStr = """
                Pose landmarks: ${poseLandmarks.landmarkCount}
                
                """.trimIndent()
            var landmarkIndex = 0
            for (landmark in poseLandmarks.landmarkList) {
                poseLandmarkStr += """	Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})
"""
                ++landmarkIndex
            }
            return poseLandmarkStr
        }
    }
}